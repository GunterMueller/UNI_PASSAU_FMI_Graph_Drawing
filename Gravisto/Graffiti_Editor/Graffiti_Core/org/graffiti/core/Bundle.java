// =============================================================================
//
//   Bundle.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.core;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;

/**
 * A generalized localized resource bundle. This class replaces {@code
 * GenericBundle}, {@code ImageBundle} and {@code StringBundle}.
 */
public final class Bundle {
    private static final String LOCALE_PREF_KEY = "locale";
    private static final Locale LOCALE = createLocale();

    private static final Map<Class<?>, WeakReference<Bundle>> BUNDLE_MAP = new HashMap<Class<?>, WeakReference<Bundle>>();

    private static ImageIcon BLANK_ICON;

    private ResourceBundle bundle;

    private Bundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    public static Bundle getCoreBundle() {
        return getBundle(Bundle.class);
    }

    /**
     * Returns the bundle of the specfied class.
     * 
     * @param clazz
     *            the class identifying the bundle.
     * @return the bundle of the specfied class.
     */
    public static Bundle getBundle(Class<?> clazz) {
        WeakReference<Bundle> ref = BUNDLE_MAP.get(clazz);

        Bundle bundle = ref == null ? null : ref.get();

        if (bundle == null) {
            ResourceBundle resourceBundle = getResourceBundle(clazz
                    .getCanonicalName());

            if (resourceBundle == null)
                return null;

            bundle = new Bundle(resourceBundle);
            BUNDLE_MAP.put(clazz, new WeakReference<Bundle>(bundle));
        }

        return bundle;
    }

    private static ResourceBundle getResourceBundle(String baseName) {
        try {
            return ResourceBundle.getBundle(baseName, LOCALE);
        } catch (MissingResourceException e) {
            return null;
        }
    }

    private static Locale createLocale() {
        try {
            Preferences preferences = Preferences
                    .userNodeForPackage(Bundle.class);

            Locale locale = null;

            String localeString = preferences.get(LOCALE_PREF_KEY, null);

            if (localeString == null) {
                locale = Locale.getDefault();
                preferences.put(LOCALE_PREF_KEY, locale.toString());
            } else {
                String[] strs = localeString.split("_");
                int len = strs.length;

                String language = len > 0 ? strs[0] : "";
                String country = len > 1 ? strs[1] : "";
                String variant = len > 2 ? strs[2] : "";
                locale = new Locale(language, country, variant);
            }

            return locale;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String format(String key, Object... args) {
        String formatString = getString(key);

        if (formatString == null)
            return null;

        return String.format(formatString, args);
    }

    /**
     * Returns the specified image icon or a blank icon, if the specified image
     * icon could not be found.
     * 
     * @param key
     *            the property name of the icon.
     * 
     * @return the specified image icon.
     */
    public ImageIcon getIcon(String key) {
        if (key == null)
            return null;

        URL path = getResource(key);

        if (path != null)
            return new ImageIcon(path);
        else
            return getBlankIcon();
    }

    /**
     * Returns the specified image or a blank image, if the specified image
     * could not be found.
     * 
     * @param key
     *            the property name of the image.
     * 
     * @return the specified image.
     */
    public Image getImage(String key) {
        return getIcon(key).getImage();
    }

    private static ImageIcon getBlankIcon() {
        if (BLANK_ICON == null) {
            BufferedImage blankImage = new BufferedImage(5, 5,
                    BufferedImage.TYPE_INT_ARGB);
            BLANK_ICON = new ImageIcon(blankImage);
        }
        return BLANK_ICON;
    }

    /**
     * Returns the specified String from the properties, {@code null} if there
     * is no such key.
     * 
     * @param key
     *            the key of the string to look up.
     * 
     * @return the value of the looked up key.
     */
    public String getString(String key) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return null;
        }
    }

    /**
     * Returns the relative location of the specified resource.
     * 
     * @param key
     *            the name of the resource.
     * 
     * @return the relative location of the specified resource.
     */
    public URL getResource(String key) {
        if (key == null)
            return null;

        String path = getString(key);

        if (path == null)
            return null;

        URL resource = Bundle.class.getClassLoader().getResource(path);
        
        if (resource == null) {
            System.err.println("Resource not found (key = " + key + ", path = " + path + ")");
        }
        
        return resource;
    }

    public boolean containsKey(String key) {
        return bundle.containsKey(key);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
