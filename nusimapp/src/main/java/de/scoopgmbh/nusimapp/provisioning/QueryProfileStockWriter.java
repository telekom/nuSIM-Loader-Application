package de.scoopgmbh.nusimapp.provisioning;

import de.scoopgmbh.nusimapp.if1.QueryProfileStockResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class QueryProfileStockWriter implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(QueryProfileStockWriter.class);

    private final PrintWriter writer;

    public QueryProfileStockWriter(Path fileName) throws IOException {
        boolean exists = Files.exists(fileName);

        writer = new PrintWriter(Files.newBufferedWriter(fileName, exists ? StandardOpenOption.APPEND : StandardOpenOption.CREATE_NEW));
        if (!exists) {
            logger.info("creating new Query Profile Stock log file {}", fileName);
            writer.println("Query Time;Queried refInfo-1;Queried refInfo-2;Queried refInfo-3;Result refInfo-1;Result refInfo-2;Result refInfo-3;Available");
        }
    }

    @Override
    public void close() {
        writer.close();
    }

    public void write(final String refInfo1, final String refInfo2, final String refInfo3, final QueryProfileStockResponse response) throws IOException {
        String now = DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneId.systemDefault()).format(Instant.now());
        String input = String.format("%s;%s;%s;%s;", now, refInfo1, refInfo2, refInfo3);
        if (response.getError() != null) {
            writer.print(input);
            writer.println(String.format("%s;%s;%s", response.getError().getErrorCode(), response.getError().getErrorMessage(), response.getError().getWaitTime()));
        } else {
            for (QueryProfileStockResponse.Stock stock : response.getResult().getStockList()) {
                writer.print(input);
                writer.println(String.format("%s;%s;%s;%s", stock.getRefInfo1(), stock.getRefInfo2(), stock.getRefInfo3(), stock.getAvailable()));
            }
        }
    }

}
