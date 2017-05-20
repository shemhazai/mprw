package com.github.shemhazai.mprw.data;

import com.github.shemhazai.mprw.domain.River;
import com.github.shemhazai.mprw.domain.RiverStatus;
import com.github.shemhazai.mprw.repo.RiverRepository;
import com.github.shemhazai.mprw.repo.RiverStatusRepository;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class DataCollectorImpl implements DataCollector {

    @Autowired
    private String baseUrl;
    @Autowired
    private RiverRepository riverRepository;
    @Autowired
    private RiverStatusRepository riverStatusRepository;

    private static final String DATE_FORMAT = "HH:mm-dd.MM.yyyy";

    private static final int DATE_COLUMN_INDEX = 0;
    private static final int LEVEL_COLUMN_INDEX = 1;

    public DataCollectorImpl() {

    }

    public void collect() throws IOException, ParseException {
        for (River river : riverRepository.selectAllRivers())
            collectFromRiver(river);
    }

    @Transactional
    private void collectFromRiver(River river) throws IOException, ParseException {
        String urlString = createUrlString(river.getName());
        Connection connection = Jsoup.connect(urlString);
        Document document = connection.get();
        for (Element row : selectTableRows(document))
            parseTableRow(river, row);
    }

    private String createUrlString(String riverName) {
        return (baseUrl.endsWith("/") ? baseUrl : baseUrl + "/")
                + "wizualizacja/punkt_pomiarowy.php?prze=" + riverName;
    }

    private Elements selectTableRows(Document document) {
        return document.select("tr[title]");
    }

    private void parseTableRow(River river, Element row) throws ParseException {
        Elements columns = selectTableColumns(row);
        if (columns.size() != 3)
            throw new ParseException("The table does not have three columns", 0);

        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        Date date = dateFormat.parse(dateString(columns));

        if (!existsRiverStatus(river.getId(), date))
            insertStatus(river.getId(), date, parseLevel(columns));
    }

    private Elements selectTableColumns(Element row) {
        return row.select("td");
    }

    private String dateString(Elements columns) {
        DateFormat formatter = new SimpleDateFormat("yyyy");
        String year = formatter.format(new Date());
        return columns.get(DATE_COLUMN_INDEX).text() + "." + year;
    }

    private boolean existsRiverStatus(int riverId, Date date) {
        return riverStatusRepository.existsRiverStatusWithRiverIdAndDate(riverId, date);
    }

    private int parseLevel(Elements columns) {
        try {
            return Integer.parseInt(columns.get(LEVEL_COLUMN_INDEX).text());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void insertStatus(int riverId, Date date, int level) {
        RiverStatus status = riverStatusRepository.createRiverStatus(riverId);
        status.setDate(date);
        status.setLevel(level);
        riverStatusRepository.updateRiverStatus(status.getId(), status);
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public RiverRepository getRiverRepository() {
        return riverRepository;
    }

    public RiverStatusRepository getRiverStatusRepository() {
        return riverStatusRepository;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setRiverRepository(RiverRepository riverRepository) {
        this.riverRepository = riverRepository;
    }

    public void setRiverStatusRepository(RiverStatusRepository riverStatusRepository) {
        this.riverStatusRepository = riverStatusRepository;
    }
}
