package com.udea.parcial;

import com.intuit.karate.junit5.Karate;

class KarateTestRunner {

    @Karate.Test
    Karate testAll() {
        return Karate.run("classpath:karate/get.feature",
                "classpath:karate/post.feature").relativeTo(getClass());
    }
}
