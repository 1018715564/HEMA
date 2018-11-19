package com.hema.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import net.sf.json.JSONArray;

@Controller
@RequestMapping("/file")
public class FileAction {
	@RequestMapping("/text")
	public void fileText(@RequestParam("file") MultipartFile file, HttpServletResponse response, HttpServletRequest req)
			throws Exception {
		PrintWriter out = response.getWriter();
		if (!file.isEmpty()) {
			String path = req.getRealPath("/sdcard/");
			System.out.println(path);
			File our = new File(path);
			File backge = new File(path);
			// �鿴�Ƿ����Ŀ¼
			String[] list = our.list();
			for (String item : list) {
				if (item.equals("sdcard")) {
					// ������ڣ���ֱ���ϴ�
					break;
				} else {
					// //�����ļ��У������ϴ�
					backge.mkdir();
					break;
				}
			}
			file.transferTo(new File(path + file.getOriginalFilename()));
			out.print("success");
		}
	}

	/**
	 * ��ѯȫ�����ϴ��ļ���Ϣ
	 */
	@RequestMapping("/all")
	public void getAllInformation(HttpServletResponse response, HttpServletRequest req) throws Exception {
		PrintWriter out = response.getWriter();
		response.setContentType("text/json;charset=utf-8");
		String path = req.getRealPath("/sdcard/");
		File file = new File(path);
		JSONArray json = new JSONArray();
		List<Object> files = null;
		// ѭ���б�
		File[] item = file.listFiles();
		for (File Item : item) {
			files = new ArrayList<Object>();
			// ��ȡ�ļ�����
			files.add(Item.getName());
			files.add("/sdcard");
			// ��ȡʱ��
			files.add(Item.lastModified());
			// ��ȡ��������
			files.add("/Hema-File/sdcard/" + Item.getName());
			json.add(files);
		}
		out.print(json);
	}

	/**
	 * ��ȡ�ļ�����Ϣ����
	 * 
	 */
	@RequestMapping("getfile/{name}")
	public ModelAndView getFile(@PathVariable("name") String name, HttpServletRequest req, HttpServletResponse response)
			throws Exception {
		// ͨ��io��������ȡ����
		String path = req.getRealPath("/sdcard/" + name + ".txt");
		ModelAndView model = new ModelAndView("fileshow");
		File file = new File(path);
		BufferedReader filename = new BufferedReader(new FileReader(path));
		List<List<Object>> item = new ArrayList<List<Object>>();
		// ��¼ÿ�е���Ϣ
		List<Object> list = null;
		// �ж��ļ��Ƿ����
		if (file.exists() && file.isFile()) {
			String line = null;
			// �ڼ���
			int page = 0;
			// ��һ�е���Ϣ��¼�ڼ�����
			while ((line = filename.readLine()) != null) {
				page++;
				list = new ArrayList<Object>();
				// �洢��������
				list.add(page);
				list.add(line);
				list.add(name + ".txt");
				item.add(list);
			}
			model.addObject("list", item);
			filename.close();
		}
		return model;
	}

	/**
	 * ɾ���ı���ĳһ������
	 */
	@RequestMapping("delete/{num}/{name}")
	public String deleteInfo(@PathVariable("num") int num, @PathVariable("name") String name, HttpServletRequest req)
			throws Exception {
		// ���ж�ȡ�ı�
		String path = req.getRealPath("/sdcard/" + name + ".txt");
		BufferedReader filename = new BufferedReader(new FileReader(path));
		// ���ֱ���
		int page = 0;
		String line = null;
		// �õ��µ������滻ԭ��������
		File newfile = new File(path);
		FileWriter fwriter = new FileWriter(newfile, true);
		List<String> list = new ArrayList<String>();
		while ((line = filename.readLine()) != null) {
			page++;
			if (page == num) {
				continue;
			} else {
				list.add(line);
			}
		}
		// ���
		FileWriter middel = new FileWriter(newfile);
		fwriter.write("");
		// ���
		for (String item : list) {
			fwriter.append(item + "\r\n");
		}
		fwriter.close();
		filename.close();
		return "forward:/file/getfile/" + name;
	}

	/**
	 * �޸�����
	 */
	@RequestMapping("update")
	public String updateInfo(String context, int id, String name, HttpServletRequest req,HttpServletResponse resp) throws IOException {
		// ��ȡ�ļ����ƣ����ж�ȡ
		String path = req.getRealPath("/sdcard/" + name);
		BufferedReader filename = new BufferedReader(new FileReader(path));
		List<String> list = new ArrayList<String>();
		String line = null;
		FileWriter fwriter = new FileWriter(new File(path), true);
		while ((line = filename.readLine()) != null) {
			// ���е����ݶ�ȡ����
			list.add(line);
		}
		// �����������
		FileWriter middel = new FileWriter(new File(path));
		fwriter.write("");
		// ͨ��ѭ��ѭ������
		int pages = 0;
		for (String item : list) {
			pages++;
			if (pages == id) {
				// ׷��ֵ
				fwriter.append(context + "\r\n");
				continue;
			} else {
				fwriter.append(item + "\r\n");
			}
		}
		fwriter.close();
		filename.close();
		name=name.substring(0, name.length()-4);
		System.out.println("�ļ�����"+name);
		return "forward:/file/getfile/" +name;
	}
}