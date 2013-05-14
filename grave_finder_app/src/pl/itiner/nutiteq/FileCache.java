package pl.itiner.nutiteq;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;

import com.nutiteq.cache.Cache;
import com.nutiteq.cache.CacheIndexDatabaseHelper;
import com.nutiteq.log.Log;
import com.nutiteq.utils.IOUtils;

public class FileCache implements Cache {
	@SuppressWarnings("unused")
	private final String caheName;
	private final File cacheDir;
	private final int cacheSize;
	@SuppressWarnings("unused")
	private final Context ctx;
	private final CacheIndexDatabaseHelper database;

	public FileCache(final Context ctx, final String caheName,
			final File cacheDir, final int cacheSize) {
		this.ctx = ctx;
		this.caheName = caheName;
		this.cacheDir = cacheDir;
		this.cacheSize = cacheSize;
		database = new CacheIndexDatabaseHelper(ctx, caheName);
	}

	public void cache(final String cacheKey, final byte[] data,
			final int cacheLevel) {
		if ((cacheLevel & Cache.CACHE_LEVEL_PERSISTENT) == 0) {
			return;
		}
		Log.debug("Cache " + cacheKey + " : " + data.length);

		final String cacheableKey = normalizeKey(cacheKey);
		Log.debug("Cache key would be: " + cacheableKey);
		final File cacheFile = new File(cacheDir, cacheableKey);
		cacheFile.getParentFile().mkdirs();

		FileOutputStream fos = null;
		final List<String> deletedFiles = new ArrayList<String>();
		try {
			fos = new FileOutputStream(cacheFile);
			fos.write(data);
			deletedFiles.addAll(database.addToIndex(cacheKey, cacheableKey,
					data.length, cacheSize));
			deleteFilesFromFileSystem(deletedFiles);
		} catch (final IOException e) {
			Log.error("Error writing " + cacheableKey);
			Log.printStackTrace(e);
		} catch (IllegalArgumentException e) {
			Log.error("Cannot save tile");
			Log.printStackTrace(e);
		} finally {
			IOUtils.closeStream(fos);
		}
	}

	private void deleteFilesFromFileSystem(final List<String> deletedFiles) {
		for (final String file : deletedFiles) {
			final File deleted = new File(cacheDir, file);
			Log.debug("Deleting " + deleted.getAbsolutePath());
			if (!deleted.delete()) {
				Log.debug("No success");
			}
		}
	}

	private String normalizeKey(final String cacheKey) {
		Pattern layerNamePattern = Pattern.compile("LAYERS=(.+?)&");
		Pattern bboxPattern = Pattern.compile("BBOX=(.+?)&");
		Matcher layerNameMatcher = layerNamePattern.matcher(cacheKey);
		Matcher bboxMatcher = bboxPattern.matcher(cacheKey);
		if (layerNameMatcher.find() && bboxMatcher.find()) {
			String layerName = layerNameMatcher.group(1);
			String bbox = bboxMatcher.group(1);
			String shortCacheKey = layerName + "_" + bbox;
			return shortCacheKey.replaceAll("://", "_").replaceAll(
					"[^a-zA-Z\\d/]", "_");
		}
		throw new IllegalArgumentException("Cannot create cache key.");
	}

	public boolean contains(final String cacheKey) {
		final boolean containsKey = database.containsKey(cacheKey);
		Log.debug("Contains 1 " + cacheKey + " : " + containsKey);
		return containsKey;
	}

	public boolean contains(final String cacheKey, final int cacheLevel) {
		final boolean contains = cacheLevel == Cache.CACHE_LEVEL_PERSISTENT
				&& contains(cacheKey);
		// Log.debug("Contains 2 " + cacheKey + " : " + contains);
		return contains;
	}

	public void deinitialize() {
		database.close();
	}

	public byte[] get(final String cacheKey) {
		Log.debug("cache load " + cacheKey);
		final String fileName = database.getRespourcePathForKey(cacheKey);

		if ("".equals(fileName)) {
			return null;
		}

		final File resource = new File(cacheDir, fileName);
		FileInputStream fis;
		try {
			fis = new FileInputStream(resource);
			return IOUtils.readFullyAndClose(fis);
		} catch (final FileNotFoundException e) {
			Log.printStackTrace(e);
			Log.debug("Could not load " + cacheKey);
			return null;
		}
	}

	public void initialize() {
		Log.debug("ZZZZZZZZZZZZZZZZZZZZZZZZZ Initialize fs cache");
		database.open();
	}
}
