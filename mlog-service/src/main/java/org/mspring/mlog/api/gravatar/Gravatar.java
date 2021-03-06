/**
 * 
 */
package org.mspring.mlog.api.gravatar;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.mspring.platform.utils.StringUtils;

/**
 * @author Gao Youbo
 * @since 2012-8-14
 * @Description
 * @TODO
 */
public final class Gravatar {

    private final static int DEFAULT_SIZE = 80;
    private final static String GRAVATAR_URL = "http://www.gravatar.com/avatar/";
    private static final GravatarRating DEFAULT_RATING = GravatarRating.GENERAL_AUDIENCES;
    private static final GravatarDefaultImage DEFAULT_DEFAULT_IMAGE = GravatarDefaultImage.HTTP_404;

    private int size = DEFAULT_SIZE;
    private GravatarRating rating = DEFAULT_RATING;
    private GravatarDefaultImage defaultImage = DEFAULT_DEFAULT_IMAGE;

    /**
     * Specify a gravatar size between 1 and 512 pixels. If you omit this, a
     * default size of 80 pixels is used.
     */
    public void setSize(int sizeInPixels) {
        Validate.isTrue(sizeInPixels >= 1 && sizeInPixels <= 512, "sizeInPixels needs to be between 1 and 512");
        this.size = sizeInPixels;
    }

    /**
     * Specify a rating to ban gravatar images with explicit content.
     */
    public void setRating(GravatarRating rating) {
        Validate.notNull(rating, "rating");
        this.rating = rating;
    }

    /**
     * Specify the default image to be produced if no gravatar image was found.
     */
    public void setDefaultImage(GravatarDefaultImage defaultImage) {
        Validate.notNull(defaultImage, "defaultImage");
        this.defaultImage = defaultImage;
    }

    /**
     * Returns the Gravatar URL for the given email address.
     */
    public String getUrl(String email) {
        Validate.notNull(email, "email");

        // hexadecimal MD5 hash of the requested user's lowercased email address
        // with all whitespace trimmed
        String emailHash = DigestUtils.md5Hex(email.toLowerCase().trim());
        String params = formatUrlParameters();
        return GRAVATAR_URL + emailHash + ".jpg" + params;
    }

    /**
     * Downloads the gravatar for the given URL using Java {@link URL} and
     * returns a byte array containing the gravatar jpg, returns null if no
     * gravatar was found.
     */
    public byte[] download(String email) throws GravatarDownloadException {
        InputStream stream = null;
        try {
            URL url = new URL(getUrl(email));
            stream = url.openStream();
            return IOUtils.toByteArray(stream);
        }
        catch (FileNotFoundException e) {
            return null;
        }
        catch (Exception e) {
            throw new GravatarDownloadException(e);
        }
        finally {
            IOUtils.closeQuietly(stream);
        }
    }

    private String formatUrlParameters() {
        List<String> params = new ArrayList<String>();

        if (size != DEFAULT_SIZE) params.add("s=" + size);
        if (rating != DEFAULT_RATING) params.add("r=" + rating.getCode());
        if (defaultImage != GravatarDefaultImage.GRAVATAR_ICON) params.add("d=" + defaultImage.getCode());

        if (params.isEmpty()) return "";
        else return "?" + StringUtils.join(params.iterator(), "&");
    }

}
