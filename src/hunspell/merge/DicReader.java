// $Id$

package hunspell.merge;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Vector;

public class DicReader extends FileReader {

  private AffReader affReader;
  private Vector<DicString> strings = new Vector<DicString>();
  private HashMap<String, DicString> hash = new HashMap<String, DicString>();

  public void addString(DicString string) {
    strings.add(string);
    hash.put(string.getValue(), string);
  }

  private void removeString(DicString string) {
    strings.remove(string);
    hash.remove(string);
  }

  @Override
  protected void readLine(String str) {
    str = str.trim();
    if (!str.equals("") && (!str.matches("\\d+"))) {
      String[] strs = str.split("/");
      addString(new DicString(strs[0], strs.length == 1 ? null : affReader.findAffixes(strs[1])));
    }
  }

  @Override
  protected void start() {

  }

  public void setAffReader(AffReader affReader) {
    this.affReader = affReader;
  }

  public void saveToFile(String fileName, AffixFlag flag)
      throws IOException {
    sort();
    StringBuilder buffer = new StringBuilder();
    buffer.append(strings.size()).append(Util.LINE_BREAK);
    for (DicString str : strings) {
      buffer.append(str.toString(flag)).append(Util.LINE_BREAK);
    }
    FileUtil.saveToFile(buffer.toString(), fileName, "UTF-8");
    buffer.setLength(0);
  }

  public void appendDic(DicReader dicReader) {
    for (DicString dicString : dicReader.strings) {
      DicString existing = hash.get(dicString.getValue());
      if (existing != null) {
        if (existing.getAffixCount() < dicString.getAffixCount()) {
          removeString(existing);
          addString(dicString);
        }
      } else
        addString(dicString);
    }
  }

  public void sort() {
    Collections.sort(strings, new Comparator<DicString>() {
      public int compare(DicString o1, DicString o2) {
        return o1.getValue().compareTo(o2.getValue());
      }
    });
  }

  public Vector<DicString> getStrings() {
    return strings;
  }

  public void clear() {
    strings.clear();
    hash.clear();
  }

  public int getWordCount() {
    return strings.size();
  }
}

