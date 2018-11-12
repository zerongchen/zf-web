package test.service;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(classes = ZongfenApplication.class)
public class LambdaTest {


    @Test
    public void test() {
       List<String> ls = Arrays.asList("11","sd","sss","ssd","ws");
       List<String> list = Arrays.asList("11","ff","ffs","ffsd","fsw3");
       List<String> beChange = new ArrayList<>();

       ls.forEach(s -> {
           if (list != null) {
                list.forEach(ll ->{
                    if (s.equals(ll)){
                        System.out.println(s);
                    }
                });
           }
       });

//        beChange = ls.stream().map(s -> {
//                list.forEach(( String ll ) ->{
//                    if(s.equals(ll)) return s;
//                    else {
//                        return ll;
//                    }
//                });
//        }).collect(Collectors.toList());

    }
}
