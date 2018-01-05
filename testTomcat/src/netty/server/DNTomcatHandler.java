package netty.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import Http.DNRequest;
import Http.DNResponse;
import Http.DNServlet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;

public class DNTomcatHandler extends ChannelInboundHandlerAdapter{
    
	private static final Map<Pattern,Class<?>> servletMapping = new HashMap<Pattern,Class<?>>(); 
	static {
		for (String  key : CustomConfig.getKeys()) {
			if (key.startsWith("servlet")) {
				String name = key.replaceFirst("servlet.", "");
				if (name.indexOf(".") != -1) {
					name = name.substring(0, name.indexOf("."));
				}else {
					continue;
				}
				String pattern = CustomConfig.getString("servlet."+ name + ".urlPattern");
				pattern = pattern.replaceAll("\\*", ".*");
				String className = CustomConfig.getString("servlet." + name + ".className");
				if (!servletMapping.containsKey(pattern)) {
					servletMapping.put(Pattern.compile(pattern), Class.forName(className));
				}				
			}
		}
	}
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof HttpRequest) {
			HttpRequest r = (HttpRequest)msg;
			DNRequest request = new DNRequest(ctx, r);
			DNResponse response = new DNResponse(ctx, r);
			//开始要做servlet
			String uri = request.getUri();
			String method = request.getMethod();
			boolean hasPattern = false;
			for (Entry<Pattern,Class<?>> entry : servletMapping.entrySet()) {
				if (entry.getKey().matcher(uri).matches()) {
					DNServlet servlet= (DNServlet)entry.getValue().newInstance();
					if ("get".equalsIgnoreCase(method)) {
						servlet.doGet(request, response);
					}else {
						servlet.doPost(request, response);
					}
					hasPattern = false;
				}
			}
			if (!hasPattern) {
				String out = String.format("404 NOTFound, URL: %s , method: %s", uri,method);
				response.write(out, 404);
			}
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}
