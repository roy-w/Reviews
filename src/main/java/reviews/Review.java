package reviews;

/**
 * Created by Roy
 */
public class Review {
    private final long id;
    private final String productId;
    private final String userId;
    private final String profileName;
    private final long helpfulnessNumerator;
    private final long helpfulnessDenominator;
    private final long score;
    private final long time;
    private final String summary;
    private final String text;

    private String translatedText;

    public Review(String line) {
        String[] splitted = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
        id = Long.parseLong(splitted[0]);
        productId = splitted[1];
        userId = splitted[2];
        profileName = splitted[3];
        helpfulnessNumerator = Long.parseLong(splitted[4]);
        helpfulnessDenominator = Long.parseLong(splitted[5]);
        score = Long.parseLong(splitted[6]);
        time = Long.parseLong(splitted[7]);
        summary = splitted[8];
        text = splitted[9];
    }

    public long getId() {
        return id;
    }

    public String getProductId() {
        return productId;
    }

    public String getUserId() {
        return userId;
    }

    public String getProfileName() {
        return profileName;
    }

    public long getHelpfulnessNumerator() {
        return helpfulnessNumerator;
    }

    public long getHelpfulnessDenominator() {
        return helpfulnessDenominator;
    }

    public long getScore() {
        return score;
    }

    public long getTime() {
        return time;
    }

    public String getSummary() {
        return summary;
    }

    public String getText() {
        return text;
    }

    public String getTranslatedText() {
        return translatedText;
    }

    public void setTranslatedText(String translatedText) {
        this.translatedText = translatedText;
    }
}
