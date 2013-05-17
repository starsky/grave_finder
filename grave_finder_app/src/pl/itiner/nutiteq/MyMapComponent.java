package pl.itiner.nutiteq;

import com.nutiteq.BasicMapComponent;
import com.nutiteq.cache.Cache;
import com.nutiteq.components.WgsPoint;
import com.nutiteq.io.ResourceDataWaiter;
import com.nutiteq.io.ResourceRequestor;
import com.nutiteq.task.TileOverlayRetriever;
import com.nutiteq.wrappers.AppContext;

public class MyMapComponent extends BasicMapComponent {

	public MyMapComponent(String arg0, AppContext arg1, int arg2, int arg3,
			WgsPoint arg4, int arg5) {
		super(arg0, arg1, arg2, arg3, arg4, arg5);
	}
	
	@Override
	public void enqueueDownload(ResourceRequestor downloadable, int cacheLevel) {
		if(downloadable instanceof TileOverlayRetriever) {
			super.enqueueDownload(new MyTileOverlayRetriever(
					(TileOverlayRetriever) downloadable), Cache.CACHE_LEVEL_PERSISTENT | Cache.CACHE_LEVEL_MEMORY);
		}
		super.enqueueDownload(downloadable, cacheLevel);
	}

	public static class MyTileOverlayRetriever implements ResourceRequestor, ResourceDataWaiter {

		private TileOverlayRetriever delegate;
		
		public MyTileOverlayRetriever(TileOverlayRetriever delegate) {
			this.delegate = delegate;
		}

		@Override
		public int getCachingLevel() {
			return Cache.CACHE_LEVEL_PERSISTENT | Cache.CACHE_LEVEL_MEMORY;
		}

		@Override
		public void dataRetrieved(byte[] data) {
			delegate.dataRetrieved(data);
		}

		@Override
		public String resourcePath() {
			return delegate.resourcePath();
		}

		@Override
		public void notifyError() {
			delegate.notifyError();
		}
	}
}
