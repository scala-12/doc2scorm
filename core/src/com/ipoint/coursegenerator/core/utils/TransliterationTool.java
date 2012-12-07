package com.ipoint.coursegenerator.core.utils;

public class TransliterationTool {

    public static final String convertRU2ENChar(char c) {
	if (Character.isUpperCase(c)) {
	    return convertSingleCharacterRU2EN(Character.toLowerCase(c))
		    .toUpperCase();
	}
	return convertSingleCharacterRU2EN(c);
    }

    /**
     * @param c
     * @return
     */
    private static String convertSingleCharacterRU2EN(char c) {
	switch (c) {
	case 'а':
	    return "a";
	case 'б':
	    return "b";
	case 'в':
	    return "v";
	case 'г':
	    return "g";
	case 'д':
	    return "d";
	case 'е':
	    return "e";
	case 'ё':
	    return "e";
	case 'ж':
	    return "zh";
	case 'з':
	    return "z";
	case 'и':
	    return "i";
	case 'й':
	    return "jj";
	case 'к':
	    return "k";
	case 'л':
	    return "l";
	case 'м':
	    return "m";
	case 'н':
	    return "n";
	case 'о':
	    return "o";
	case 'п':
	    return "p";
	case 'р':
	    return "r";
	case 'с':
	    return "s";
	case 'т':
	    return "t";
	case 'у':
	    return "u";
	case 'ф':
	    return "f";
	case 'х':
	    return "h";
	case 'ц':
	    return "c";
	case 'ч':
	    return "ch";
	case 'ш':
	    return "sh";
	case 'щ':
	    return "shh";
	case 'ъ':
	    return "";
	case 'ы':
	    return "y";
	case 'ь':
	    return "";
	case 'э':
	    return "eh";
	case 'ю':
	    return "ju";
	case 'я':
	    return "ya";
	}
	return new String(new char[] { c });
    }

    public static final String convertRU2ENString(String ruString) {
	if (ruString != null) {
	    StringBuffer result = new StringBuffer();
	    char[] charArray = ruString.toCharArray();
	    for (int i = 0; i < charArray.length; i++) {
		result.append(convertRU2ENChar(charArray[i]));
	    }
	    return result.toString();
	} else {
	    return null;
	}
    }
}
