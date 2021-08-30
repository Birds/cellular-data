package com.birds.cellulardata.controllers;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.birds.cellulardata.service.DatabaseService;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

@Controller
public class UploadController {
	
	private final static Logger LOGGER = Logger.getLogger(UploadController.class.getName());

	@Autowired
	DatabaseService databaseService;
	
	
	/**
	 * @param file
	 * The cellular data usage CSV file
	 * Expected Columns [Format]: Date [Jan 20 2021], Data [250.12 MB/GB], Charge [$0.00]
	 *  
	 * @param model
	 * For now, absolutely meaningless. Will eventually return status code, intended use as API endpoint.
	 * 
	 * @return
	 * Again, will eventually return a status code.
	 */
	@PostMapping("/upload")
	public String upload(@RequestParam MultipartFile file, Model model) {
		
		if(file.isEmpty()) {
			return "Empty File Provided";
		}
		
		String user = getFileame(file);
		
		List<String[]> parsedCSV;
		try {
			Reader reader = new InputStreamReader(file.getInputStream());
			CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
			parsedCSV = csvReader.readAll();
			updateDatabase(user, parsedCSV);
		} catch (IOException e) {
			LOGGER.severe("IOException Error");
			e.printStackTrace();
		} catch (CsvException e) {
			LOGGER.severe("CSVException Error");
			e.printStackTrace();
		} catch (ParseException e) {
			LOGGER.severe("Date Parsing Error");
			e.printStackTrace();
		}
	    
		return "Upload Complete";
	}
	
	private String getFileame(MultipartFile file) {
		String result = file.getOriginalFilename().substring(0,file.getOriginalFilename().indexOf(".csv"));
		result = result.replaceAll("[^A-Za-z]+", "");
		return result;
	}
	
	private void updateDatabase(String user, List<String[]> data) throws ParseException {
		for(String[] entry: data) {
			Date date=new SimpleDateFormat("MMM dd yyyy").parse(entry[0]);
			long epoch = date.getTime();
			double dataUsage = Double.parseDouble(entry[1].replaceAll("[^\\d.]", ""));
			if(entry[1].contains("GB")) {
				dataUsage *= 1000;
			}else if(!entry[1].contains("MB")) {
				LOGGER.severe("Unexpected Data Unit Found: " + entry[1]);
			}
			
			databaseService.insertData(user, epoch, dataUsage);

		}
		
		
	}
	
}
