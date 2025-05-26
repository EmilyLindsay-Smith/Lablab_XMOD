package xmod.view;

public class ScreenWords{
	/** Empty constructor, object does not need to be instantiated as all methods in class are static */
	public ScreenWords()
	{
		//empty
	}

    /**
     * Returns list of words
     */
    public static String[] getWords(){
        String[] words = {
            "Hello World",
            "নিরুপদ্রব",
            "পরিতক্ত",
            "অবিশ্বাসিনী",
            "নিস্তব্ধতা",
            "ভবিষ্যৎ",
            "শ্রেষ্ঠতা",
            "আকাঙ্ক্ষিত",
            "প্রাদেশিক",
            "ক্রমাগত",
            "স্বতন্ত্রতা",
            "مرحبا",
            "হ্যালো",
            "Здравствуйте"
            };
        return words;
    }

    /**
     * Returns default font
     */
    public static String getDefaultFont(){
        return "Times New Roman";
    }

    /**
     * Returns default size
     */
    public static int getDefaultSize(){
        return 50;
    }

    /**
	 * Searches for needle in haystack
	 * @param needle string to search for
	 * @param haystack array to search through
	 * @return index of needle in haystack (or -1 if not found)
	 */
    public static Integer getIndex(String needle, String[] haystack){
        for (int index = 0; index < haystack.length; index++){
            if (haystack[index] == needle){
                return index;
            }
        }
        return -1;
    }

}