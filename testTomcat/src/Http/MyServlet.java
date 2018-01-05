package Http;

public class MyServlet extends DNServlet{
    
	
	@Override
	public void doGet(DNRequest request, DNResponse response) {
		super.doGet(request, response);
	}

	@Override
	public void doPost(DNRequest request, DNResponse response) {
		System.out.println("这就是我们自己的业务逻辑");
		String param = "hello";
		String val = request.getParameter("param");
		response.write(param + ":" + val , 200);
	}
}
