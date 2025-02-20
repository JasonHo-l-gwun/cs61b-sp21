package capers;

import java.io.File;
import java.io.IOException;

import static capers.Utils.*;

/** A repository for Capers 
 * @author TODO
 * The structure of a Capers Repository is as follows:
 *
 * .capers/ -- top level folder for all persistent data in your lab12 folder
 *    - dogs/ -- folder containing all of the persistent data for dogs
 *    - story -- file containing the current story
 *
 * TODO: change the above structure if you do something different.
 */
public class CapersRepository {
    /** Current Working Directory. */
    static final File CWD = new File(System.getProperty("user.dir"));

    /** Main metadata folder. */
    static final File CAPERS_FOLDER = join(CWD,".capers"); // TODO Hint: look at the `join`
                                            //      function in Utils

    /**
     * Does required filesystem operations to allow for persistence.
     * (creates any necessary folders or files)
     * Remember: recommended structure (you do not have to follow):
     *
     * .capers/ -- top level folder for all persistent data in your lab12 folder
     *    - dogs/ -- folder containing all of the persistent data for dogs
     *    - story -- file containing the current story
     */
    public static void setupPersistence() throws IOException {
        // TODO 初始化
        if (!CAPERS_FOLDER.exists()) CAPERS_FOLDER.mkdir();
        File dogsFolder = join(CAPERS_FOLDER, "dogs");
        if (!dogsFolder.exists()) dogsFolder.mkdir();
        File storyFile = join(CAPERS_FOLDER, "story");
        if (!storyFile.exists()) storyFile.createNewFile();
    }

    /**
     * Appends the first non-command argument in args
     * to a file called `story` in the .capers directory.
     * @param text String of the text to be appended to the story
     */
    public static void writeStory(String text) {
        // TODO 把文本写入story文件
        File storyFile = join(CAPERS_FOLDER, "story");

        String currentStory = Utils.readContentsAsString(storyFile);
        String updatedStory = currentStory + text + "\n";
        Utils.writeContents(storyFile, updatedStory);
        System.out.println(updatedStory);
    }

    /**
     * Creates and persistently saves a dog using the first
     * three non-command arguments of args (name, breed, age).
     * Also prints out the dog's information using toString().
     */
    public static void makeDog(String name, String breed, int age) throws IOException {
        // TODO 新建一个狗对象,并保存到dogs
        Dog dog = new Dog(name, breed, age);
        dog.saveDog();
    }

    /**
     * Advances a dog's age persistently and prints out a celebratory message.
     * Also prints out the dog's information using toString().
     * Chooses dog to advance based on the first non-command argument of args.
     * @param name String name of the Dog whose birthday we're celebrating.
     */
    public static void celebrateBirthday(String name) throws IOException {
        // TODO 给狗庆祝生日,并把更新后的信息保存
        Dog dog =Dog.fromFile(name);
        dog.haveBirthday();
        dog.saveDog();
    }
}
