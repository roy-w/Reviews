package reviews;

import translations.TranslationService;
import translations.TranslationTask;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

/**
 * Created by Roy
 */
public class Start {
    private ExecutorService executor = Executors.newFixedThreadPool(100);
    private TranslationService translationService = new TranslationService();

    public static void main(String[] args) {
        if (args.length <1){
            System.err.println("Usage:  -PappArgs=\"['csvFileName', 'translate=true']\"\n");
            System.exit(0);
        }
        String csvFile = args[0];
        boolean translate = false;
        if (args.length>1){
            String translateStr = args[1];
            if (translateStr.startsWith("translate=")){
                translate = Boolean.parseBoolean(translateStr.substring("translate=".length()));
            }
        }
        try {
            Start start = new Start();
            start.start(csvFile,translate);
            System.exit(0); //Gradlew not exitting gracefully at the end
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public void start(String csvFileName, boolean translate) throws FileNotFoundException {
        long limit = 1000;

        List<Review> reviews = readReviewsFile(csvFileName);
        Map<String,Long> activeUsers = getTop(reviews, Review::getUserId, limit);
        System.out.println(activeUsers);

        Map<String,Long> activeProducts = getTop(reviews, Review::getProductId, limit);
        System.out.println(activeProducts);

        // Can improve the extractor to remove non alphanumeric chars
        Function<Review, String []> wordsExtractor = (review -> (review.getSummary() + " "+review.getText()).toLowerCase().split("\\s+"));
        Map<String,Long> topWords = getTopFlat(reviews, wordsExtractor, limit);
        System.out.println(topWords);

        if (translate){
            translateReviews(reviews);
        }
    }

    private void translateReviews(List<Review> reviews) {
        reviews.stream().forEach(review -> executor.submit(new TranslationTask(review, translationService)));
        System.out.println("Done translating");
    }

    private List<Review> readReviewsFile(String csvFileName) throws FileNotFoundException {
        InputStream is = new FileInputStream(new File(csvFileName));
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        List<Review> reviews = br.lines()
                .skip(1)
                .parallel().map(Review::new).collect(Collectors.toList());
        return reviews;
    }

    private Map<String,Long> getTop(Collection<Review> reviews, Function<Review, String> keyGetter, long limit){
        Map<String,Long> countByUserId = reviews
                .parallelStream()
                .collect(groupingBy(keyGetter, counting()));
        return sortByValue(countByUserId, limit);
    }

    private Map<String,Long> getTopFlat(Collection<Review> reviews, Function<Review, String []> stringSplitter, long limit){
        Map<String,Long> countByUserId = reviews
                .parallelStream()
                .map(stringSplitter)
                .flatMap(Arrays::stream)
                .collect(groupingBy(identity(), counting()));;
        return sortByValue(countByUserId, limit);
    }

    private Map<String,Long> sortByValue(Map<String,Long> unsortedMap, long limit){
        SortedMap<String, Long> orderedResult = new TreeMap<>();
        unsortedMap.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(limit)
                .forEachOrdered(x -> orderedResult.put(x.getKey(), x.getValue()));
        return orderedResult;
    }


}
