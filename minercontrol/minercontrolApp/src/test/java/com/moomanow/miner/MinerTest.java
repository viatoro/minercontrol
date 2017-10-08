package com.moomanow.miner;

import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.moomanow.miner.api.pool.impl.YaampPool;
import com.moomanow.miner.config.bean.ConfigMinerBean;
import com.moomanow.miner.config.bean.ConfigUserBean;

import ognl.MemberAccess;
import ognl.Ognl;
import ognl.OgnlException;

public class MinerTest {

	@Test
	public void test_method_1() {
		
//		MinerProcess minerProcess = new MinerProcess();
//		
//		minerProcess.start();
		
		
//		Map<String,Object> root = new HashMap<>();
//		try {
//			root.put("pool", new YaampPool());
//			root.put("user", new ConfigUserBean());
//			root.put("alg", "neo");
//			Map context = Ognl.createDefaultContext(root, mem);
//			System.out.println(Ognl.getValue("pool", context, root));
//
//			System.out.println(Ognl.getValue("pool", context, root));
////			String host = (String) Ognl.getValue(api.getConfigPoolBean().getHostFormat(), context,root,String.class);
////			String port = (String) Ognl.getValue(api.getConfigPoolBean().getPortFormat(), context,root,String.class);
////			String user = (String) Ognl.getValue(api.getConfigPoolBean().getUserFormat(), context,root,String.class);
////			String password = (String) Ognl.getValue(api.getConfigPoolBean().getPasswordFormat(), context,root,String.class);
//			
////			System.out.println(Ognl.getValue("pool", context, root));
////			System.out.println(host);
////			System.out.println(port);
////			System.out.println(user);
////			System.out.println(password);
////			appMiner.run(alg, host, port, user, password);
//		} catch (OgnlException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		Set<String> listAlg = new HashSet<>();
//		
//		listAlg.add("test");
//		listAlg.add("test2");
//		listAlg.add("test3");
//		listAlg.add("test4");
//		
//		listAlg.stream().forEach((String alg) -> {
//			System.out.println(alg);
//		});
	}
	
	

	private MemberAccess mem = new MemberAccess() {

		@Override
		public Object setup(Map context, Object target, Member member, String propertyName) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void restore(Map context, Object target, Member member, String propertyName, Object state) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean isAccessible(Map context, Object target, Member member, String propertyName) {
			// TODO Auto-generated method stub
			return true;
		}
	};

}
