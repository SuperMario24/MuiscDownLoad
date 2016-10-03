package my.supermario.muiscdownload;

public class SongUrl {

	private String file_link;
	private int file_size;
	public SongUrl(String file_link, int file_size) {
		super();
		this.file_link = file_link;
		this.file_size = file_size;
	}
	public String getFile_link() {
		return file_link;
	}
	public void setFile_link(String file_link) {
		this.file_link = file_link;
	}
	public int getFile_size() {
		return file_size;
	}
	public void setFile_size(int file_size) {
		this.file_size = file_size;
	}
	
}
