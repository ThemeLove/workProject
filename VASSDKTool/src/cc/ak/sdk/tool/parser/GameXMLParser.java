
package cc.ak.sdk.tool.parser;

import cc.ak.sdk.tool.bean.Game;

import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 游戏解析器
 */
public class GameXMLParser {

    public static Map<String, Game> parser(String xmlPath) {
        Map<String, Game> gameMap = new HashMap<String, Game>();
        try {
            SAXReader saxReader = new SAXReader();
            Element root = saxReader.read(new File(xmlPath)).getRootElement();
            List<Element> gameList = root.elements();
            if (gameList == null || gameList.size() == 0) {
                System.out.println("没有配置游戏，请配置" + xmlPath);
                System.exit(0);
            } else {
                System.out.println("================游戏列表================");
                System.out.println("gameId | 游戏名称");
                System.out.println("------|-------");
                for (Element gameElement : root.elements()) {
                    Game game = new Game();
                    game.setId(gameElement.attributeValue("id"));
                    game.setName(gameElement.attributeValue("name"));
                    game.setFolder(gameElement.attributeValue("folder"));
                    game.setGameId(gameElement.attributeValue("gameId"));
                    game.setPlatpormId(gameElement.attributeValue("platpormId"));
                    game.setSubPlatformId(gameElement.attributeValue("subPlatformId"));
                    game.setUrl(gameElement.attributeValue("url"));
                    game.setOrientation(gameElement.attributeValue("orientation"));
                    game.setStatisticUrl(gameElement.attributeValue("statisticUrl"));
                    game.setGameMainName(gameElement.attributeValue("gameMainName"));
                    game.setDebug(gameElement.attributeValue("debug"));
                    game.setChannelId(gameElement.attributeValue("channelId"));
                    game.setSubChannelId(gameElement.attributeValue("subChannelId"));
                    System.out.println(game);
                    gameMap.put(game.getId(), game);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return gameMap;
    }

}
