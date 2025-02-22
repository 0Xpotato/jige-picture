package com.jige.jigepicturebackend.manager;

import com.jige.jigepicturebackend.config.CosClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class FileManager {  
  
    @Resource
    private CosClientConfig cosClientConfig;
  
    @Resource  
    private CosManager cosManager;  
  
    // ...  
}
