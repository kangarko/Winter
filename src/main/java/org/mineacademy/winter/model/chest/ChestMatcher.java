package org.mineacademy.winter.model.chest;

public interface ChestMatcher {

	String getValidFormatExample();

	boolean isValidFormat(String[] lines);

	String getPermission();
}
