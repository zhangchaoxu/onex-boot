package com.nb6868.onex.uc.service;

import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.uc.dao.PostDao;
import com.nb6868.onex.uc.dto.PostDTO;
import com.nb6868.onex.uc.entity.PostEntity;
import org.springframework.stereotype.Service;

/**
 * 岗位
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class PostService extends DtoService<PostDao, PostEntity, PostDTO> {

}
