package com.liu.service;

import com.liu.entity.AppInfo;
import com.liu.repository.AppInfoRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AppInfoService {

    private static final Logger logger = LoggerFactory.getLogger(AppInfoService.class);
    private static  Integer pageNum = 20;

    public static final String SPIDER_IDENTITY = "sj.qq.com";

    @Autowired
    AppInfoRepository appInfoRepository;

    public void analysis(){
        Integer pageSize = 1;
        while(true){
            logger.info("当前页码数--》{}",pageSize);
            PageRequest pageRequest = new PageRequest(pageSize - 1, pageNum, null);
            Page<AppInfo> appinfoList = appInfoRepository.findAll(pageRequest);
            if(appinfoList.getContent().size() == 0){
                break;
            }
            List<AppInfo> result = doModify(appinfoList.getContent());
            appInfoRepository.save(result);
            pageSize ++;
        }
    }

    private List<AppInfo> doModify(List<AppInfo> appinfoList) {
        List<AppInfo> result = new ArrayList<AppInfo>();

        for (AppInfo appInfo: appinfoList) {
            result.add(crawler(appInfo));
        }
        return result;
    }

    private AppInfo crawler(AppInfo appInfo) {
       String detailUrl = String.format("http://%s/myapp/detail.htm?apkName=%s", SPIDER_IDENTITY, appInfo.getAppPackage());
        String _html = com.liu.utils.HttpUtils.get(detailUrl);
        if (_html.contains("search-none-img-box"))
            return appInfo;
        Document docApp = Jsoup.parse(_html);
        Elements imagesELe = docApp.select("span#J_PicTurnImgBox div.pic-img-box > img");
        if(imagesELe!=null && imagesELe.size() !=0){
            List<String> image = new ArrayList<String>();
            for (Element ele: imagesELe) {
                image.add(ele.attr("data-src"));
            }
            appInfo.setImages(image.toString());
        }
        return appInfo;
    }

}
