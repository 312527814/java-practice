package com.my.fegin;

import feign.Feign;
import feign.Param;
import feign.RequestLine;

import java.util.List;

interface GitHub {
    @RequestLine("GET /repos/{owner}/{repo}/contributors")
    String contributors(@Param("owner") String owner, @Param("repo") String repo);

    @RequestLine("POST /repos/{owner}/{repo}/issues")
    void createIssue(Issue issue, @Param("owner") String owner, @Param("repo") String repo);

}

class Contributor {
    String login;
    int contributions;
}

class Issue {
    String title;
    String body;
    List<String> assignees;
    int milestone;
    List<String> labels;
}


