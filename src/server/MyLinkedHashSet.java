package server;

import java.util.concurrent.CopyOnWriteArraySet;
import java.util.Iterator;
import java.util.Date;
import java.util.stream.Collectors;

public class MyLinkedHashSet{
    Date date;
    private CopyOnWriteArraySet<Room> set;
    MyLinkedHashSet(){
        set = new CopyOnWriteArraySet<>();
        date = new Date();
    }

    /**
     * Метод, который выводит коллекцию
     */
    String show(){
        Iterator<Room> iter = set.iterator();
        String ans = "";
        while(iter.hasNext()){
            ans = ans + "\n" +(iter.next().toString());
        }
        return ans;
    }


    /**
     * Метод, который добавляет заданный элемент
     * @param room
     * @return
     */
    public boolean add (Room room){
        set.add(room);
        return true;
    }

    /**
     * Метод, который удаляет заданный элемент
     * @param room
     */
    void remove (Room room){
        set.remove(room);
    }

    /**
     * Метод, который удаляет все элементы из коллекции
     */
    public void clear (){
        set.clear();
        //System.out.println("Из коллекции удалены все элементы");
    }
    /**
     * Метод, добавляющий элемент, переданный в качестве параметра, в коллекцию, если он меньше всех элементов коллекции
     * @param room
     */
    void add_if_min (Room room){
        Iterator<Room> iter = set.iterator();
        boolean pr = false;
        while(iter.hasNext()){
            if (room.compareTo(iter.next()) >= 0)
                pr = true;
        }
        if (pr == false)
            set.add(room);
    }

    /**
     * Метод, удаляющий из коллекции все элементы, которые больше элемента, переданного в качестве параметра
     * @param room
     */
    void remove_greater(Room room){
        set = new CopyOnWriteArraySet<>(set.stream().filter(s -> s.compareTo(room) < 0).collect(Collectors.toSet())) ;
    }

    public CopyOnWriteArraySet<Room> getSet() {
        return set;
    }

    /**
     * Метод, который выводит информацию о коллекции: время инициализации, количество элементов
     * @return
     */
    String info(){
        return (("Коллекция LinkelHashSet, тип объектов Room") + ("Время инициализации: " + date.toString()) + ("Количество элементов: " + set.size()));

    }
}