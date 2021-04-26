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

package de.scoopgmbh;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.*;
import java.util.stream.Collector;

import static java.util.Objects.requireNonNull;

/**
 * A {@link java.util.stream.Stream} Collector that will take all elements of a stream and passes them to a processor in batches of a given max size
 * <p>
 * This allows for e.g. batch inserts of stream elements into a database.
 * <p>
 * Furthermore, for every batch processing call, an optional state item can be passed. This state item will be returned when {@link java.util.stream.Stream#collect(Collector)} is called.
 * The state allows for keeping track of information. Think about number of processed elements, number of processed batches, etc.
 * <p>
 * The stream is processed in order, as long as it is not a parallel stream.
 *
 * @param <T> type of stream elements
 * @param <U> type of the state element
 */
public class BatchCollector<T, U> implements Collector<T, List<T>, U> {

    private final int batchSize;
    private final BiFunction<List<T>, U, U> batchProcessor;
    private U state;

    /**
     * Constructs the batch collector
     *
     * @param batchSize      the batch size after which the batchProcessor should be called
     * @param state          an (initialized) item that is passed to the batchProcessor and will be returned when the stream is processed
     * @param batchProcessor the batch processor which accepts batches of records to process. The processor is never called for empty batches!
     */
    public BatchCollector(int batchSize, @Nullable U state, @Nonnull BiFunction<List<T>, U, U> batchProcessor) {
        if (batchSize <= 0) {
            throw new IllegalArgumentException("batchSize must be greater than 0");
        }
        this.batchProcessor = requireNonNull(batchProcessor);
        this.state = state;
        this.batchSize = batchSize;
    }

    public Supplier<List<T>> supplier() {
        return () -> new ArrayList<>(batchSize);
    }

    public BiConsumer<List<T>, T> accumulator() {
        return (ts, t) -> {
            ts.add(t);
            if (ts.size() >= batchSize) {
                state = batchProcessor.apply(ts, state);
                ts.clear();
            }
        };
    }

    public BinaryOperator<List<T>> combiner() {
        return (ts, ots) -> {
            // process each parallel list without checking for batch size
            // avoids adding all elements of one to another
            // can be modified if a strict batching mode is required
            if (!ts.isEmpty()) {
                state = batchProcessor.apply(ts, state);
            }
            if (!ots.isEmpty()) {
                state = batchProcessor.apply(ots, state);
            }
            return Collections.emptyList();
        };
    }

    public Function<List<T>, U> finisher() {
        return ts -> {
            if (!ts.isEmpty()) {
                state = batchProcessor.apply(ts, state);
            }
            return state;
        };
    }

    public Set<Characteristics> characteristics() {
        return Collections.emptySet();
    }
}
