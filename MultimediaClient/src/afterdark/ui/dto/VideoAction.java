package afterdark.ui.dto;

public class VideoAction {
	private String action;
	private String video;
	private String proto = null;
	public VideoAction(String action, String video, String proto) {
		super();
		this.action = action;
		this.video = video;
		this.proto = proto;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getVideo() {
		return video;
	}
	public void setVideo(String video) {
		this.video = video;
	}
	public String getProto() {
		return proto;
	}
	public void setProto(String proto) {
		this.proto = proto;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return action+" "+video+" with "+proto;
	}
}