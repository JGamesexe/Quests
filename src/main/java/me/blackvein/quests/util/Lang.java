/*******************************************************************************************************
 * Continued by FlyingPikachu/HappyPikachu with permission from _Blackvein_. All rights reserved.
 * 
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN
 * NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************************************/

package me.blackvein.quests.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.blackvein.quests.Quests;

public class Lang {

	public static String iso = "em-US";
	private static final LangToken tokens = new LangToken();
	public static final LinkedHashMap<String, String> langMap = new LinkedHashMap<String, String>();
	private final Quests plugin;

	public Lang(Quests plugin) {
		tokens.initTokens();
		this.plugin = plugin;
	}

	public static String get(String key) {
		return langMap.containsKey(key) ? tokens.convertString(langMap.get(key)) : "NULL";
	}

	public static String getKey(String val) {
		for (Entry<String, String> entry : langMap.entrySet()) {
			if (entry.getValue().equals(val)) {
				return entry.getKey();
			}
		}
		return "NULL";
	}

	public static String getCommandKey(String val) {
		for (Entry<String, String> entry : langMap.entrySet()) {
			if (entry.getValue().equalsIgnoreCase(val) && entry.getKey().toUpperCase().startsWith("COMMAND_")) {
				return entry.getKey();
			}
		}
		return "NULL";
	}

	public static void clearPhrases() {
		langMap.clear();
	}

	public static int getPhrases() {
		return langMap.size();
	}

	public static String getModified(String key, String[] tokens) {
		String orig = langMap.get(key);
		for (int i = 0; i < tokens.length; i++) {
			orig = orig.replaceAll("%" + (i + 1), tokens[i]);
		}
		return orig;
	}

	public void loadLang() throws InvalidConfigurationException, IOException {
		File langFile = new File(plugin.getDataFolder(), File.separator + "lang" + File.separator + iso + File.separator + "strings.yml");
		File langFile_new = new File(plugin.getDataFolder(), File.separator + "lang" + File.separator + iso + File.separator + "strings_new.yml");
		LinkedHashMap<String, String> allStrings = new LinkedHashMap<String, String>();
		FileConfiguration config = new YamlConfiguration();
		FileConfiguration config_new = new YamlConfiguration();
		if (langFile.exists() && langFile_new.exists()) {
			config = loadYamlUTF8(langFile);
			config_new = loadYamlUTF8(langFile_new);
			//Load user's lang file and determine new strings
			for (String key : config.getKeys(false)) {
				allStrings.put(key, config.getString(key));
				config_new.set(key, null);
			}
			//Add new strings and notify user
			for (String key : config_new.getKeys(false)) {
				String value = config_new.getString(key);
				if (value != null) {
					allStrings.put(key, config_new.getString(key));
					plugin.getLogger().warning("There are new language phrases in /lang/" + iso + "/strings_new.yml for the current version!"
							+ " You must transfer them to, or regenerate, strings.yml to remove this warning!");
				}
			}
			config_new.options().header("Below are any new strings for your current version of Quests! Transfer them to the strings.yml of the"
					+ " same folder to stay up-to-date and suppress console warnings.");
			config_new.options().copyHeader(true);
			config_new.save(langFile_new);
			langMap.putAll(allStrings);
		} else {
			plugin.getLogger().severe("Failed loading lang files for " + iso + " because they were not found. Using default en-US");
			iso = "en-US";
			config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource("strings.yml"), "UTF-8"))); //TODO better than loadYamlUTF*() ?
			for (String key : config.getKeys(false)) {
				allStrings.put(key, config.getString(key));
			}
			langMap.putAll(allStrings);
		}
		plugin.getLogger().info("Loaded language " + iso + ". Translations via Crowdin");
	}
	
	/**
	 * Load YAML file using UTF8 format to allow extended characters
	 * @param file system file in YAML format
	 * @return yaml
	 * @throws InvalidConfigurationException
	 * @throws IOException
	 */
	public static YamlConfiguration loadYamlUTF8(File file) throws InvalidConfigurationException, IOException {
		StringBuilder sb = new StringBuilder((int) file.length());
		
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		char[] buf = new char[1024];
		int l;
		while ((l = in.read(buf, 0, buf.length)) > -1) {
			sb = sb.append(buf, 0, l);
		}
		in.close();
		
		YamlConfiguration yaml = new YamlConfiguration();
		yaml.loadFromString(sb.toString());
		return yaml;
	}

	private static class LangToken {

		Map<String, String> tokenMap = new HashMap<String, String>();

		public void initTokens() {
			tokenMap.put("%br%", "\n");
			tokenMap.put("%tab%", "\t");
			tokenMap.put("%rtr%", "\r");
			tokenMap.put("%bold%", ChatColor.BOLD.toString());
			tokenMap.put("%italic%", ChatColor.ITALIC.toString());
			tokenMap.put("%underline%", ChatColor.UNDERLINE.toString());
			tokenMap.put("%strikethrough%", ChatColor.STRIKETHROUGH.toString());
			tokenMap.put("%magic%", ChatColor.MAGIC.toString());
			tokenMap.put("%reset%", ChatColor.RESET.toString());
			tokenMap.put("%white%", ChatColor.WHITE.toString());
			tokenMap.put("%black%", ChatColor.BLACK.toString());
			tokenMap.put("%aqua%", ChatColor.AQUA.toString());
			tokenMap.put("%darkaqua%", ChatColor.DARK_AQUA.toString());
			tokenMap.put("%blue%", ChatColor.BLUE.toString());
			tokenMap.put("%darkblue%", ChatColor.DARK_BLUE.toString());
			tokenMap.put("%gold%", ChatColor.GOLD.toString());
			tokenMap.put("%gray%", ChatColor.GRAY.toString());
			tokenMap.put("%darkgray%", ChatColor.DARK_GRAY.toString());
			tokenMap.put("%pink%", ChatColor.LIGHT_PURPLE.toString());
			tokenMap.put("%purple%", ChatColor.DARK_PURPLE.toString());
			tokenMap.put("%green%", ChatColor.GREEN.toString());
			tokenMap.put("%darkgreen%", ChatColor.DARK_GREEN.toString());
			tokenMap.put("%red%", ChatColor.RED.toString());
			tokenMap.put("%darkred%", ChatColor.DARK_RED.toString());
			tokenMap.put("%yellow%", ChatColor.YELLOW.toString());
		}

		public String convertString(String s) {
			for (String token : tokenMap.keySet()) {
				s = s.replace(token, tokenMap.get(token));
				s = s.replace(token.toUpperCase(), tokenMap.get(token));
			}
			return s;
		}
	}
}