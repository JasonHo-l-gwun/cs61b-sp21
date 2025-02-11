package gh2;

import edu.princeton.cs.introcs.StdAudio;
import edu.princeton.cs.introcs.StdDraw;

public class GuitarHero {
    public static void main(String[] args) {
        String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
        GuitarString[] strings = new GuitarString[37];

        // 初始化每个音符的 GuitarString
        for (int i = 0; i < 37; i++) {
            // 计算每个音符频率
            double frequency = 440.0 * Math.pow(2, (i - 24) / 12.0);
            strings[i] = new GuitarString(frequency);
        }

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                // 获取按键索引
                int index = keyboard.indexOf(key);

                if (index != -1) {
                    // 弹奏音符
                    strings[index].pluck();
                }
            }

            // 合成所有声音
            double sample = 0;
            for (GuitarString string : strings) {
                string.tic();
                sample += string.sample();
            }

            StdAudio.play(sample);  // 播放合成音频
        }
    }
}