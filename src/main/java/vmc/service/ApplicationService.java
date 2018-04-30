package vmc.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ApplicationService {
	
	private static final long MAX_UPLOAD_FILE_VIDEO_SIZE = 32000000;
	
	private static final long MAX_UPLOAD_FILE_PHOTO_SIZE = 22000000;
	
    public boolean checkFileVideoSize(MultipartFile v) { return (v.getSize() < MAX_UPLOAD_FILE_VIDEO_SIZE); }
    
    public boolean checkFilePhotoSize(MultipartFile f) { return (f.getSize() < MAX_UPLOAD_FILE_PHOTO_SIZE); }
    
}