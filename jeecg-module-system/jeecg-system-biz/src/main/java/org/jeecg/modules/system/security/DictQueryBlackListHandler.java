package org.jeecg.modules.system.security;

import org.jeecg.common.constant.SymbolConstant;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.common.util.security.AbstractQueryBlackListHandler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 字典组件 执行sql前校验 只校验表字典
 * dictCodeString格式如：
 * table,text,code
 * table where xxx,text,code
 * table,text,code, where xxx
 *
 * @Author taoYan
 * @Date 2022/3/23 21:10
 **/
@Component("dictQueryBlackListHandler")
public class DictQueryBlackListHandler extends AbstractQueryBlackListHandler {

    @Override
    protected List<QueryTable> getQueryTableInfo(String dictCodeString) {
        if (dictCodeString != null && dictCodeString.indexOf(SymbolConstant.COMMA) > 0) {
            String[] arr = dictCodeString.split(SymbolConstant.COMMA);
            if (arr.length != 3 && arr.length != 4) {
                return null;
            }
            String tableName = getTableName(arr[0]);
            QueryTable table = new QueryTable(tableName, "");
            // 无论什么场景 第二、三个元素一定是表的字段，直接add
            table.addField(arr[1].trim());
            String filed = arr[2].trim();
            if (oConvertUtils.isNotEmpty(filed)) {
                table.addField(filed);
            }
            List<QueryTable> list = new ArrayList<>();
            list.add(table);
            return list;
        }
        return null;
    }

    /**
     * 取where前面的为：table name
     *
     * @param str
     * @return
     */
    private String getTableName(String str) {
        String[] arr = str.split("\\s+(?i)where\\s+");
        String tableName = arr[0];
        //【20230814】解决使用参数tableName=sys_user t&复测，漏洞仍然存在
        if (tableName.contains(" ")) {
            tableName = tableName.substring(0, tableName.indexOf(" "));
        }
        //【issues/4393】 sys_user , (sys_user), sys_user%20, %60sys_user%60
        String reg = "\\s+|\\(|\\)|`";
        return tableName.replaceAll(reg, "");
    }

}
