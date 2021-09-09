package com.github.learndifferent.mtm;

import com.github.learndifferent.mtm.dto.WebForSearchDTO;
import com.github.learndifferent.mtm.dto.WebsiteDTO;
import com.github.learndifferent.mtm.query.WebFilter;
import com.github.learndifferent.mtm.service.WebsiteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest
class MtmApplicationTests {

    @Autowired
    private WebsiteService websiteService;

    @Test
    void webFilterTest() throws ParseException {
        WebFilter webFilter = new WebFilter();
        webFilter.setLoad(6);

        List<String> usernames = new ArrayList<>();
        usernames.add("145613221");
//        usernames.add("261262163126");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<Date> dates = new ArrayList<>();
//        dates.add(sdf.parse("2021-04-06"));
//        dates.add(sdf.parse("2021-03-31"));

//        webFilter.setIfOrderByTime(true);
//        webFilter.setDesc(true);

        webFilter.setUsernames(usernames);
        webFilter.setDates(dates);

        System.out.println(webFilter);

        List<WebsiteDTO> webs = websiteService.findWebsitesDataByFilter(webFilter);
        webs.forEach(w -> System.out.println(w.getCreateTime()));
    }

    @Test
    void webForSearchTest() {
        List<WebForSearchDTO> allWebForSearch = websiteService.getAllWebsitesDataForSearch();
        allWebForSearch.forEach(System.out::println);
    }
}
