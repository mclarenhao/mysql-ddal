package org.mysql.ddal.client;

import org.mysql.ddal.protocol.protocol.mysql41.MySQLColumnDefinitionPacket;
import org.mysql.ddal.protocol.protocol.mysql41.MySQLErrorPacket;
import org.mysql.ddal.protocol.protocol.mysql41.MySQLResultSetHeaderPacket;
import org.mysql.ddal.protocol.protocol.mysql41.MySQLRowDataPacket;
import org.mysql.ddal.query.MySQLQueryCommand;
import org.mysql.ddal.query.MySQLQueryHandler;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Hello world!
 *
 */
@Slf4j
public class App {
	@SneakyThrows
	public static void main(String[] args) {
		DefaultMySQLConnection connection = new DefaultMySQLConnection("127.0.0.1", 3306, "root", "123456",
				"freeswitch", "UTF-8");
		connection.executeQuery(new MySQLQueryCommand("select * from user"), new MySQLQueryHandler() {
			@Override
			public void onError(MySQLErrorPacket error) {
				log.error("错误::" + error.getErrorMessage());
			}

			@Override
			public void onReceiveHeader(MySQLResultSetHeaderPacket headerPacket) {
				System.out.println("=====================================================");
			}

			@Override
			public void onReceiveColumnDefinitions(MySQLColumnDefinitionPacket definitionPacket) {
				System.out.print(definitionPacket.getName() + "\t");
			}

			@Override
			public void onReceiveColumnDefinitionsCompleted() {
				System.out.println();
			}

			@Override
			public void onReceiveRowData(MySQLRowDataPacket rowDataPacket) {
				for (int i = 0; i < rowDataPacket.fieldCount; i++) {
					System.out.print(new String(rowDataPacket.getFieldValues().get(i)) + "\t");
				}
				System.out.println();
			}

			@Override
			public void onEnd() {
				super.onEnd();
			}

		});

	}
}
