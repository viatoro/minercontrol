package com.moomanow.miner;

import java.util.Arrays;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;

import com.moomanow.miner.appminer.IAppMiner;
import com.moomanow.miner.bean.RevenueBean;
import com.moomanow.miner.dao.MinerControlDao;

@SpringBootApplication
@ImportResource("classpath:quartz-context.xml")
public class Application {
	
	private static Map<RevenueBean, IAppMiner> runing;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        
		Thread Thread = new Thread(()->{ 
			try {
				java.lang.Thread.sleep(200);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            System.out.println("Shouting down ...");
			runing.entrySet().parallelStream().forEach((miner) -> {
				try {
					miner.getValue().destroy();
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			
//			processBenchmarking.parallelStream().forEach((process) ->{
//				try {
//					process.destroy();
//				}catch (Exception e) {
//					e.printStackTrace();
//				}
//			});
//			processMining.parallelStream().forEach((process) ->{
//				try {
//					process.destroy();
//				}catch (Exception e) {
//					e.printStackTrace();
//				}
//			});
			System.out.println("Shouted Doew");
		}, "Shutdown-thread");
		Runtime.getRuntime().addShutdownHook(Thread );
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {

            System.out.println("Let's inspect the beans provided by Spring Boot:");

            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            for (String beanName : beanNames) {
                System.out.println(beanName);
            }
            MinerControlDao minerControlDao= ctx.getBean(MinerControlDao.class);
            runing = minerControlDao.getRuning();
        };
    }

}
