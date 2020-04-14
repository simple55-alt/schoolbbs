package cdu.zb.controller;


import cdu.zb.constants.GlobalConstants;
import cdu.zb.dto.ReplyDto;
import cdu.zb.entity.BanEntity;
import cdu.zb.entity.ReplyEntity;
import cdu.zb.entity.TopicEntity;
import cdu.zb.entity.UserEntity;
import cdu.zb.jsonresult.BaseApiController;
import cdu.zb.jsonresult.JsonResult;
import cdu.zb.response.ReplyResponse;
import cdu.zb.service.BanService;
import cdu.zb.service.ReplyService;
import cdu.zb.service.TopicService;
import cdu.zb.service.UserService;
import cdu.zb.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author accountw
 * @since 2020-01-17
 */
@RestController
@RequestMapping("/api/reply")
public class ReplyController extends BaseApiController {

    @Autowired
    private ReplyService replyService;

    @Autowired
    private TopicService topicService;
    @Autowired
    private BanService banService;

    @Autowired
    private UserService userService;

    @GetMapping(value = "/getReplybyTopicid" ,name = "通过帖子id获取回复")
    public JsonResult<List<ReplyResponse>> getReplybyTopicid(String topicid,Integer index) throws UnsupportedEncodingException {
        return jr(GlobalConstants.SUCCESS,"获取成功",replyService.getReplybyTopicid(topicid,index));
    }

    @PostMapping(value = "/saveReply",name="保存评论")
    public JsonResult<Integer> saveReply(@RequestBody ReplyDto replyDto) throws UnsupportedEncodingException {
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String username=authentication.getName();
        UserEntity userEntity=userService.getOne(new QueryWrapper<UserEntity>().eq("username",username));
        BanEntity banEntity=banService.getOne(new QueryWrapper<BanEntity>().eq("uid",userEntity.getId()));
        if(banEntity!=null){
            return  jr(GlobalConstants.NO_UNAUTHORIZED,banEntity.getFreeTime().toString());
        }
        Base64.Encoder encoder = Base64.getEncoder();
        replyDto.setContext(encoder.encodeToString(replyDto.getContext().getBytes("UTF-8")));
          return  jr(GlobalConstants.SUCCESS,"保存成功",replyService.saveReply(replyDto));
    }

    @PostMapping(value = "/savePicture",name="保存图片")
    public JsonResult<String> saveReply(MultipartFile file){
        // 文件保存路径
        String id=IdUtil.createID();
        String fileName = file.getOriginalFilename();
        String[] name=fileName.split("\\.");
        String a=name[name.length-1];
        String path="D:\\schoolbbs\\schoolbbs-web\\public\\image\\";
        String filePath = path + id+"."+a;
        if (!file.isEmpty()) {
            try {

                // 转存文件
                file.transferTo(new File(filePath));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return  jr(GlobalConstants.SUCCESS,"保存成功",id+"."+a);
    }

    @GetMapping(value = "/deleteReply",name = "删除评论")
    public JsonResult<Integer> deleteReply(String id){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String username=authentication.getName();
        UserEntity userEntity=userService.getOne(new QueryWrapper<UserEntity>().eq("username",username));
        ReplyEntity replyEntity=replyService.getById(id);
        TopicEntity topicEntity=topicService.getById(replyEntity.getTopicId());
        if(replyService.remove(new QueryWrapper<ReplyEntity>().eq("id",id)
        .eq("user_id",userEntity.getId()))){
            userService.deleteExp(userEntity.getId(),1);
            topicService.deletecount(replyEntity.getTopicId());
            return  jr(GlobalConstants.SUCCESS,"删除成功");
        }
        if(topicEntity.getUserId().equals(userEntity.getId())){
            if(replyService.removeById(id)){
                userService.deleteExp(replyEntity.getUserId(),1);
                topicService.deletecount(replyEntity.getTopicId());
                return  jr(GlobalConstants.SUCCESS,"删除成功");
            }
        }
        return  jr(GlobalConstants.ERROR,"删除失败");
    }
}
