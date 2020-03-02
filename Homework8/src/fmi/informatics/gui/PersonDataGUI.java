package fmi.informatics.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

import fmi.informatics.comparators.AgeComparator;
import fmi.informatics.comparators.EgnComparator;
import fmi.informatics.comparators.HeightComparator;
import fmi.informatics.comparators.NameComparator;
import fmi.informatics.comparators.PersonComparator;
import fmi.informatics.comparators.WeightComparator;
import fmi.informatics.enums.EType;
import fmi.informatics.extending.Person;
import fmi.informatics.extending.Professor;
import fmi.informatics.extending.Student;
import fmi.informatics.util.FileReader;

// създаваме клас PersonDataGUI
public class PersonDataGUI {
	
	public static Person[] people;
	JTable table;
	PersonDataModel personDataModel;
	
	static JTextArea textArea = new JTextArea("");
	static JTextArea textAreaMap = new JTextArea("");
	static JTextArea textAreaSorted = new JTextArea("");
	static List<Student> students = new ArrayList<>();
	static Map<Integer, Student> studentsMap = new HashMap<>();

	public static void main(String[] args) {
//		getPeople();
		
		// Извикваме писането/четенето във файл
//		initializeData();
		
		createAndGetDetailsForAllStudents();
		sortList();
		
		PersonDataGUI gui = new PersonDataGUI();
		gui.createAndShowGUI();
	}
	
	// Добавяме писането/четенето във файл
	private static void initializeData() {
		if (!FileReader.isFileExists()) {
			FileReader.createPersonFile();
		}
		
		FileReader.writePeople(people);
	}
	
	public static void getPeople() {
		people = new Person[8];
		
		for (int i = 0; i < 4; i++) {
			Person student = Student.StudentGenerator.make();
			people[i] = student;
		}
		
		for (int i = 4; i < 8; i++) {
			Person professor = Professor.ProfessorGenerator.make();
			people[i] = professor;
		}
	}
	
	public void createAndShowGUI() {
		JFrame frame = new JFrame("Таблица с данни за хора");
		frame.setSize(500, 1000);
		
		JLabel label = new JLabel("Списък с потребители", JLabel.CENTER);
		
		personDataModel = new PersonDataModel(people);
		table = new JTable(personDataModel);
		
		JScrollPane scrollPane = new JScrollPane(table);
		
		// Добавяме бутон за сортиране по години с Comparable interface
		JButton buttonSortAge = new JButton("Сортирай по години");

		// Добавяме бутон за сортиране
		JButton buttonSort = new JButton("Сортирай");
		
		// Добавяме бутон за филтриране
		JButton buttonFilter = new JButton("Филтрирай");
		
		// Добавяме панел, където ще поставим бутоните
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.add(buttonSortAge);
		buttonsPanel.add(buttonSort);
		buttonsPanel.add(buttonFilter);
//		JPanel textAreaPanel = new JPanel();
//		JTextArea textArea = new JTextArea("");
//		textAreaPanel.add(textArea, BorderLayout.CENTER);
//		JScrollPane scrollTextArea = new JScrollPane();
//		scrollTextArea.setBounds(0, 0, 490, 250);
		textArea.setLineWrap(true);
		textArea.setForeground(Color.BLACK);
		textArea.setBackground(Color.WHITE);
		textAreaMap.setLineWrap(true);
		textAreaMap.setForeground(Color.BLACK);
		textAreaMap.setBackground(Color.WHITE);
//		scrollTextArea.add(textArea);
//		scrollTextArea.add(textAreaMap);
		
		Container container = frame.getContentPane();
		container.setLayout(new GridLayout(5, 1));
//		container.add(label);
		container.add(scrollPane);
		container.add(textArea);
		container.add(textAreaMap);
		container.add(textAreaSorted);
		// Добавяме панелът с бутоните в контейнера
		container.add(buttonsPanel);
		
		// Добавяме listener към бутона за сортиране по години
		buttonSortAge.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Arrays.sort(people);
				table.repaint();
			}
		});
		
		// Добавяме диалог
		final JDialog sortDialog = new CustomDialog(getSortText(), this, EType.SORT);
		
		// Добавяме диалог за филтрацията
		final JDialog filterDialog = new CustomDialog(getFilterText(), this, EType.FILTER);
		
		// Добавяме listener към бутона за сортиране
		buttonSort.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sortDialog.pack();
				sortDialog.setVisible(true);
			}
		});
		
		// Добавяме listener за филтрация
		buttonFilter.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				filterDialog.pack();
				filterDialog.setVisible(true);
			}
		});
		
		frame.setVisible(true);
	}
	
	// Добавяме метод за филтриране
	public void filterTable(int intValue, JTable table, Person[] people) {

		switch (intValue) {
			case 1: 
				this.personDataModel = new PersonDataModel(filterData(people, Student.class));
				break;
			case 2: 
				this.personDataModel = new PersonDataModel(filterData(people, Professor.class));
				break;
			case 3: 
				this.personDataModel = new PersonDataModel(filterData(people, Person.class));
				break;
		}
		
		table.setModel(this.personDataModel);
		table.repaint();	
	}
	
	private Person[] filterData(Person[] persons, Class<?> clazz) {
		ArrayList<Person> filteredData = new ArrayList<>();
		
		for (int i = 0; i < persons.length; i++) {
			
			if (clazz == Person.class) {
				// Тук обхващаме филтрирането на "Други"
				if (!(persons[i] instanceof Student) && !(persons[i] instanceof Professor)) {
					filteredData.add(persons[i]);
				}
			} else if (clazz.isInstance(persons[i])) { // Филтриране по студент или професор
				filteredData.add(persons[i]);
			}
		}
		
		// Преобразуваме списъка в масив
		Person[] filteredDataArray = new Person[filteredData.size()];
		filteredDataArray = filteredData.toArray(filteredDataArray);
		return filteredDataArray;
	}

	public void sortTable(int intValue, JTable table, Person[] people) {
		PersonComparator comparator = null;
		
		switch (intValue) {
			case 1: 
				comparator = new NameComparator(); 
				break;
			case 2: 
				comparator = new EgnComparator();
				break;
			case 3:
				comparator = new HeightComparator();
				break;
			case 4: 
				comparator = new WeightComparator();
				break;
			case 5:
				comparator = new AgeComparator();
				break;
		}

		if (comparator == null) { // Ако стойността е null - сортирай по подразбиране
			Arrays.sort(people); // Сортировка по подразбиране по години
		} else {
			Arrays.sort(people, comparator);
		}
		
		table.repaint();	
	}
	
	private static String getSortText() {
		return "Моля, въведете цифрата на колоната, по която да се сортират данните: \n" +
				" 1 - Име \n" +
				" 2 - ЕГН \n" +
				" 3 - Височина \n" +
				" 4 - Тегло \n" +
				" 5 - Години \n"; 
	}
	
	// Добавяме текст, който да се визуализира в диалога за филтриране
	private static String getFilterText(){
		return "Моля, въведете цифрата на филтъра, който искате да използвате: \n" +
			   " 1 - Студенти \n" +
			   " 2 - Преподаватели \n" + 
			   " 3 - Други \n";
	}
	
	private static void createAndGetDetailsForAllStudents() {
		people = new Person[5];
		
		for (int i = 0; i < 5; i++) {
			Student student = Student.StudentGenerator.make();
			people[i] = student;
			students.add(student);
			studentsMap.put(i, student);
		}
		textArea.setText("Text Area List: \n");
		students.forEach(item->{
			textArea.append("name: "+ item.getName() + "Uni: "+ item.getUniversity()
						+ "Spec: "+ item.getSpeciality()+"Fac.Num.: "+ item.getFacNumber());
		});
		
		textAreaMap.setText("Text Area Map: \n");
		studentsMap.forEach((k,v)->{
			textAreaMap.append("name: "+ v.getName() + "Uni: "+ v.getUniversity()
			+ "Spec: "+ v.getSpeciality()+"Fac.Num.: "+ v.getFacNumber());
		});
	}
	
	private static void sortList() {
		 students.sort(
			      (Student s1, Student s2) -> s1.getName().compareTo(s2.getName()));
		 //another implementation
		 //students.sort((s1, s2) -> s1.getName().compareTo(s2.getName()));
		 textAreaSorted.setText("Text Area Sorted: \n");
		 students.forEach(item->{
				textAreaSorted.append("name: "+ item.getName() + "Uni: "+ item.getUniversity()
							+ "Spec: "+ item.getSpeciality()+"Fac.Num.: "+ item.getFacNumber());
		});
	}
}
