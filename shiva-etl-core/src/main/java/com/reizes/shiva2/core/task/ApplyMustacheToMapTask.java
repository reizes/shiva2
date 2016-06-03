package com.reizes.shiva2.core.task;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Map;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.reizes.shiva2.core.AfterProcessAware;
import com.reizes.shiva2.core.BeforeProcessAware;
import com.reizes.shiva2.core.context.ProcessContext;

public class ApplyMustacheToMapTask extends AbstractTask implements BeforeProcessAware, AfterProcessAware {
	private MustacheFactory mf = new DefaultMustacheFactory();
	private Mustache mustache;
	private Map<String, Object> functions;
	private String scriptEngineName = "nashorn";
	private String templateFilePath;
	private Reader templateFileReader;
	private Invocable invocable;
	
	public ApplyMustacheToMapTask() {
		
	}
	
	public ApplyMustacheToMapTask(String templateFilePath) {
		this.setTemplateFilePath(templateFilePath);
	}
	
	public ApplyMustacheToMapTask(String scriptEngineName, String templateFilePath) {
		this.setScriptEngineName(scriptEngineName);
		this.setTemplateFilePath(templateFilePath);
	}

	public Map<String, Object> getFunctions() {
		return functions;
	}

	public ApplyMustacheToMapTask setFunctions(Map<String, Object> functions) {
		this.functions = functions;
		return this;
	}

	public String getScriptEngineName() {
		return scriptEngineName;
	}

	public ApplyMustacheToMapTask setScriptEngineName(String scriptEngineName) {
		this.scriptEngineName = scriptEngineName;
		return this;
	}

	public String getTemplateFilePath() {
		return templateFilePath;
	}

	public ApplyMustacheToMapTask setTemplateFilePath(String templateFilePath) {
		this.templateFilePath = templateFilePath;
		return this;
	}

	private void compileCustomFunctions() {
		StringBuilder sb = new StringBuilder();
		if (functions != null) {
			for (String name : functions.keySet()) {
				sb.append("function ").append(name).append("(data) ");
				sb.append(functions.get(name)).append(";");
			}
		}

		if (sb.length() > 0) {
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine engine = manager.getEngineByName(scriptEngineName);
			try {
				engine.eval(sb.toString());
			} catch (ScriptException e) {
				e.printStackTrace();
			}

			invocable = (Invocable) engine;
		}
	}
	
	private void invokeFunction(Map<String, Object> data) throws NoSuchMethodException, ScriptException {
		if (functions != null) {
			for (String name : functions.keySet()) {
				data.put(name, invocable.invokeFunction(name, data));
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object doProcess(Object input) throws Exception {
		Map<String, Object> data = (Map<String, Object>) input;
		invokeFunction(data);
		StringWriter writer = new StringWriter();
		this.mustache.execute(writer, data);
		String result = writer.toString();
		writer.close();
		return result;
	}
	
	@Override
	public void onAfterProcess(ProcessContext context, Object data) throws Exception {
		if (this.templateFileReader != null) {
			this.templateFileReader.close();
		}
	}
	@Override
	public void onBeforeProcess(ProcessContext context, Object data) throws Exception {
		compileCustomFunctions();
		this.templateFileReader = new InputStreamReader(new FileInputStream(this.templateFilePath));
		this.mustache = mf.compile(this.templateFileReader, "MapMustacheTemplateApplyTransformerMap");
	}

}
