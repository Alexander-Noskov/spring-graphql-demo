package com.example.graphql.controller;

import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;

@GraphQlTest(BookController.class)
class BookControllerTest {

//    @Autowired
//    private GraphQlTester graphQlTester;
//
//    @Test
//    void books() {
//        graphQlTester
//                .documentName("books")
//                .execute()
//                .path("books")
//                .entityList(Book.class)
//                .hasSize(2);
//    }
}