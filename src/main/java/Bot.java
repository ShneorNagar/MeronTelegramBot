import org.telegram.telegrambots.api.methods.send.SendDocument;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Bot extends TelegramLongPollingBot {

    static ReportsManager reportsManager = new ReportsManager();
    static boolean isNewReport = false;
    static boolean isGetReport = false;
    private String donateStatus;
    private BotUtils botUtils;
    private File report;
    private List<String> reportType = new ArrayList<>(Arrays.asList("דוח חדש", "דוח קיים"));
    private final String fileTypeMessage = "תרצה שאכתוב לך לדוח חדש או קיים?";
    static String newReportRequestMessage = "אוקיי.. בכיף!\n" +
            "רק תן לי בבקשה שם לדוח.\n" +
            "אבל בבקשה שהשם יתחיל במילה 'דוח', כדי שאוכל להבין זאת \uD83E\uDD16";
    private final String dontSkip = "היי.. יש סדר בליובאוויטש.. \n" +
            "לא לדלג שלבים בבקשה \uD83D\uDE4F";
    static ProssesEscorter prossesEscorter = new ProssesEscorter();

    public void onUpdateReceived(Update update) {


        if (update.getMessage().hasText() && update.hasMessage()) {

            Long chatId = update.getMessage().getChatId();
            String firstName = update.getMessage().getChat().getFirstName();
            String lastName = update.getMessage().getChat().getLastName();
            String messageFromClint = update.getMessage().getText();
            botUtils = new BotUtils();
            HashMap<String, String> donationMap;
            ExcelWriter excelWriter;

            /**
             * starting point of the bot
             * asking user if want to add new donation
             * or to produce report by name*/
            if (messageFromClint.equals("היי") || messageFromClint.equals("שלום")) {

                botUtils.sendTextMessage(chatId, "ברוך הבא " + firstName + " " + lastName);

                List<String> options = new ArrayList<>(Arrays.asList("דוח חדש", "דוח קיים"));

                botUtils.sendReplyKeyboard(chatId, options, "אנא בחר באחת מהאפשרויות");
            }
            /**
             * to add new donation
             * from two options
             * cash or gift
             * */
            else if (messageFromClint.equals("דוח חדש")) {

                List<String> paymentMethods = new ArrayList<>(Arrays.asList("מזומן", "מתנה"));
                String message = "בחר סוג תרומה בבקשה";
                botUtils.sendReplyKeyboard(chatId, paymentMethods, message);
            }
            /**
             * in case user choose to donate with cash
             * */
            else if (messageFromClint.equals("מזומן")) {

                prossesEscorter.completeDonationType();
                donateStatus = DonateStatusEnum.CASH.getEnumValue();
                botUtils.sendReplyKeyboard(chatId, reportType, fileTypeMessage);
            }
            /**
             * in case user choose to donate with gift
             * */
            else if (messageFromClint.equals("מתנה")) {

                prossesEscorter.completeDonationType();
                donateStatus = DonateStatusEnum.GIFT.getEnumValue();
                botUtils.sendReplyKeyboard(chatId, reportType, fileTypeMessage);
            }

            /**
             * in case user choose to write current donation to new report
             * */
            else if (messageFromClint.equals("דוח חדש")) {

                isNewReport = true;

                if (prossesEscorter.donationTypeCompleted) {
                    botUtils.sendTextMessage(chatId, newReportRequestMessage);
                    prossesEscorter.completeFileReportType();
                } else {
                    botUtils.sendTextMessage(chatId, dontSkip);
                }
            }
            /**
             * in case user choose to write donation to existing report
             * */
            else if (messageFromClint.equals("דוח קיים")) {

                isNewReport = false;
                reportsManager.loadFiles();

                if (prossesEscorter.donationTypeCompleted) {
                    prossesEscorter.completeFileReportType();
                    sendReplayKeyBordToUser(chatId, reportsManager.getAllReportsNames(), "בחר דוח מהרשימה");
                } else {
                    botUtils.sendTextMessage(chatId, dontSkip);
                }

                /**
                 * report handler
                 * */
            } else if (messageFromClint.startsWith("דוח")) {

                if (isGetReport) {
                    File report = reportsManager.getExistingReportByName(botUtils, chatId, messageFromClint);
                    try {
                        sendReportToUser(chatId, report);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (isNewReport) {
                        report = reportsManager.getReportToSave(botUtils, chatId, messageFromClint);
                    } else {
                        report = reportsManager.getExistingReportByName(botUtils, chatId, messageFromClint);
                    }
                    prossesEscorter.completeReportName();
                    requestForDonation(chatId);
                }
                isGetReport = false;
            }

            /**
             * in case user choose to produce previous report
             * bot will send all report names
             * user will select from ReplayKeyBord
             * */
            else if (messageFromClint.equals("דוח קיים")) {

                reportsManager.loadFiles();
                sendReplayKeyBordToUser(chatId, reportsManager.getAllReportsNames(), "בחר דוח מהרשימה");
                isGetReport = true;
            }
            /**
             * this is may be donation message
             * if no, message of unknown command will send to user
             * */
            else {
                if (prossesEscorter.donationTypeCompleted && prossesEscorter.fileReportTypeCompleted && prossesEscorter.reportNameCompleted) {

                    donationMap = botUtils.convertDonationToMap(chatId, messageFromClint);

                    excelWriter = new ExcelWriter(report, donationMap, donateStatus, isNewReport);
                    excelWriter.perform();

                    initVariablesAndSendEndingMessage(chatId);
                } else {
                    sendMessageToUser(chatId, dontSkip);
                }
            }
        }

    }

    private void initVariablesAndSendEndingMessage(Long chatId) {
        sendMessageToUser(chatId, "אוקיי..\n" +
                "שמרתי לך את התרומה בקובץ " + report.getName()); // todo add emoji or picture for gritting
        prossesEscorter = new ProssesEscorter();
        String donateStatus = null;
        isNewReport = false;
        isGetReport = false;
        report = null;

    }

    private void requestForDonation(Long chatId) {
        botUtils.sendTextMessage(chatId, "שלח לי בבקשה את רשימת התרומות בצורה הבאה.\n" +
                "תורם - סכום\n" +
                "תורם - סכום\n" +
                "וכו'..");
    }

    private void sendMessageToUser(Long chatId, String message) {
        botUtils.sendTextMessage(chatId, message);
    }

    private void sendReplayKeyBordToUser(Long chatId, List<String> keys, String message) {
        botUtils.sendReplyKeyboard(chatId, keys, message);
    }

    public void sendReportToUser(Long chatId, File fileToSend) throws TelegramApiException {

        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(chatId);
        sendDocument.setNewDocument(fileToSend);
        sendDocument.setCaption("הנה הדוח");
        sendDocument(sendDocument);
    }

    public String getBotUsername() {
        return "meronBot";
    }

    public String getBotToken() {
        return "926814310:AAEjdF_lCjsFTVBZXfhFyoiPnUxrtE-HfZE";
    }
}
