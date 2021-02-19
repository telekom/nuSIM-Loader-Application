/*
 * nusim-loader
 *
 * (c) 2020 Deutsche Telekom AG.
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package de.scoopgmbh.nusimapp.server.api.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.commons.lang.StringUtils;
import org.reactivestreams.Publisher;
import ratpack.stream.Streams;

import java.time.Duration;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WebsocketLogAppender extends AppenderBase<ILoggingEvent> {
    private static final int LOG_LINE_MAX_LENGTH = 500;
    private final ObjectWriter ow = new ObjectMapper().writer();
    private final int bufferSize;
    private final Consumer<ILoggingEvent> discardHandler;
    private final Publisher<String> stream;
    private LinkedList<ILoggingEvent> buffer = new LinkedList<>();

    public WebsocketLogAppender(final int bufferSize, final Duration pollInterval, final Consumer<ILoggingEvent> discardHandler, Function<ILoggingEvent, ?> mapper) {
        this.bufferSize = bufferSize;
        this.discardHandler = discardHandler;

        final Function<ILoggingEvent, ?> theMapper = mapper != null ? mapper : new Function<ILoggingEvent, LogEvent>() {
            @Override
            public LogEvent apply(ILoggingEvent in) {
                return new LogEvent(in.getLevel().levelStr, StringUtils.abbreviate(in.getFormattedMessage(), LOG_LINE_MAX_LENGTH), new Date(in.getTimeStamp()));
            }
        };

        stream = Streams.periodically(Executors.newSingleThreadScheduledExecutor(), pollInterval, (i) -> {
            final List<ILoggingEvent> list = buffer;
            buffer = new LinkedList<>();
            String s = ow.writeValueAsString(list.stream().map(theMapper).collect(Collectors.toList()));
            return s;
        });

    }

    public Publisher<String> getStream() {
        return stream;
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        if (eventObject.getLevel().levelStr.equals("TRACE")) {
            return;
        }
        buffer.addLast(eventObject);

        while (buffer.size() > bufferSize) {
            ILoggingEvent event = buffer.removeFirst();
            discardHandler.accept(event);
        }
    }

    private static final class LogEvent {
        final String loglevel;
        final String message;
        final Date t;

        private LogEvent(String loglevel, String message, Date t) {
            this.loglevel = loglevel;
            this.message = message;
            this.t = t;
        }

        public String getLoglevel() {
            return loglevel;
        }

        public String getMessage() {
            return message;
        }

        public Date getT() {
            return t;
        }
    }
}
