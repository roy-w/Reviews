package translations;

import com.google.common.base.Splitter;
import reviews.Review;

import java.util.Iterator;

/**
 * Created by Roy
 */
public class TranslationTask implements Runnable {
    private final Review review;
    private final TranslationService translationService;
    private final static int CHAR_LIMIT = 1000;

    public TranslationTask(Review review, TranslationService translationService) {
        this.review = review;
        this.translationService = translationService;
    }

    @Override
    public void run() {
        String text = review.getSummary()+" "+review.getText();
        try {
            Iterable<String> chunks = getChunks(text);
            StringBuffer translation = new StringBuffer();
            Iterator<String> it = chunks.iterator();
            while (it.hasNext()){
                String chunk = it.next();
                translation.append(translationService.translate(chunk, "en", "fr") + " ");
            }
            review.setTranslatedText(translation.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Can be improved to check for full words and limit break before the limit if we pass it
    private Iterable<String> getChunks(String text){
        return Splitter.fixedLength(CHAR_LIMIT).split(text);
    }
}
