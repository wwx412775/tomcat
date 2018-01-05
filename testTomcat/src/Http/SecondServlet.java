package Http;

import java.util.List;
import java.util.Map;

public class SecondServlet extends DNServlet{

	@Override
	public void doGet(DNRequest request, DNResponse response) {
		super.doGet(request, response);
	}

	@Override
	public void doPost(DNRequest request, DNResponse response) {
		String params = JSON.toJSONString(request.getParameters(), true);
	//	String val = request.getParameter("param");
		response.write(params , 200);
	}
	
	

}
