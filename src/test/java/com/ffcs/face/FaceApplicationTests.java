package com.ffcs.face;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FaceApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void testHash(){
        String msg = "23821379210";
        for (int i = 0; i < 5; i++) {
            int hashCode = msg.hashCode();
            System.out.println(hashCode);
        }
    }
}
