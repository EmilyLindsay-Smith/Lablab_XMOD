package xmod.view;

/** ScreenWords holds the set of words used in FontWindow.
 * @author ELS
 * @version 2.0
 * @since 2025-06-09
 */

public final class ScreenWords {
    /** Default font size. */
    static final int DEFAULT_FONT_SIZE = 150;
    /** Default font. */
    static final String DEFAULT_FONT = "Times New Roman";

    /** Empty constructor.
     * object does not need to be instantiated
     * as all methods in class are static */
    private ScreenWords() { }

    /**
     * Returns list of words.
     * Many of these are Bengali to check correct rendering of diacritics
     * @return list of word
     */
    public static String[] getWords() {
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
     * Returns default font.
     * @return this.DEFAULT_FONT
     */
    public static String getDefaultFont() {
        return DEFAULT_FONT;
    }

    /**
     * Returns default font size.
     * @return this.DEFAULT_FONT_SIZE
     */
    public static int getDefaultSize() {
        return DEFAULT_FONT_SIZE;
    }

    /**
     * Searches for needle in haystack.
     * @param needle string to search for
     * @param haystack array to search through
     * @return index of needle in haystack (or -1 if not found)
     */
    public static Integer getIndex(final String needle,
                                    final String[] haystack) {
        for (int index = 0; index < haystack.length; index++) {
            if (haystack[index] == needle) {
                return index;
            }
        }
        return -1;
    }

}
