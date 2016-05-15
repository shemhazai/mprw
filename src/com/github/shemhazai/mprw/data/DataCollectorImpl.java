package com.github.shemhazai.mprw.data;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.github.shemhazai.mprw.domain.River;
import com.github.shemhazai.mprw.domain.RiverStatus;
import com.github.shemhazai.mprw.repo.RiverRepository;
import com.github.shemhazai.mprw.repo.RiverStatusRepository;

@Component
public class DataCollectorImpl implements DataCollector {

	@Autowired
	private String baseUrl;
	@Autowired
	private RiverRepository riverRepository;
	@Autowired
	private RiverStatusRepository riverStatusRepository;

	public DataCollectorImpl() {

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

	public void collect() throws IOException, ParseException {
		for (River river : riverRepository.selectAllRivers())
			collectFromRiver(river);
	}

	@Transactional
	private void collectFromRiver(River river) throws IOException, ParseException {
		String year = Calendar.getInstance().get(Calendar.YEAR) + "";
		String urlString = createUrlString(river.getName());

		Connection connection = Jsoup.connect(urlString);
		Document document = connection.get();

		Elements rows = document.select("tr[title]");
		for (Element row : rows) {
			Elements columns = row.select("td");
			if (columns.size() != 3)
				throw new ParseException("The table does not have three columns", 0);

			DateFormat dateFormat = new SimpleDateFormat("HH:mm-dd.MM.yyyy");
			Date date = dateFormat.parse(columns.get(0).text() + "." + year);

			if (riverStatusRepository.existsRiverStatusWithRiverIdAndDate(river.getId(), date))
				continue;

			int level = Integer.parseInt(columns.get(1).text());

			RiverStatus riverStatus = riverStatusRepository.createRiverStatus(river.getId());
			riverStatus.setDate(date);
			riverStatus.setLevel(level);
			riverStatusRepository.updateRiverStatus(riverStatus.getId(), riverStatus);
		}
	}

	private String createUrlString(String riverName) {
		return (baseUrl.endsWith("/") ? baseUrl : baseUrl + "/") + "wizualizacja/punkt_pomiarowy.php?prze=" + riverName;
	}
}