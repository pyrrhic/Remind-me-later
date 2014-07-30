package com.angulartest.utilities;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReminderFormatter {		
	protected List<String> getMatches(String regexPattern, String searchString) {
		Pattern pattern = Pattern.compile(regexPattern); 
		Matcher matcher = pattern.matcher(searchString);
		
		LinkedList<String> matches = new LinkedList<String>();
		while(matcher.find()) {
			matches.add(matcher.group());
		}	
		
		return matches;
	}
}
