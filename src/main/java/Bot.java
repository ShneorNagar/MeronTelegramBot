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
    private List<String> reportType = new ArrayList<>(Arrays.asList("new report", "existing report"));
    private final String fileTypeMessage = "and the report will be written to?...";
    static String newReportRequestMessage = "please give me the report name " +
            "i want to know that is new report so please write at the beginning" +
            "'report'";
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
            if (messageFromClint.equals("/start")) {

                botUtils.sendTextMessage(chatId, "welcome " + firstName + " " + lastName);

                List<String> options = new ArrayList<>(Arrays.asList("add new donation", "get report by name"));

                botUtils.sendReplyKeyboard(chatId, options, "please select an option");
            }
            /**
             * to add new donation
             * from two options
             * cash or gift
             * */
            else if (messageFromClint.equals("add new donation")) {

                List<String> paymentMethods = new ArrayList<>(Arrays.asList("cash", "gift"));
                String message = "please select payment method";
                botUtils.sendReplyKeyboard(chatId, paymentMethods, message);
            }
            /**
             * in case user choose to donate with cash
             * */
            else if (messageFromClint.equals("cash")) {

                prossesEscorter.completeDonationType();
                donateStatus = DonateStatusEnum.CASH.getEnumValue();
                botUtils.sendReplyKeyboard(chatId, reportType, fileTypeMessage);
            }
            /**
             * in case user choose to donate with gift
             * */
            else if (messageFromClint.equals("gift")) {

                prossesEscorter.completeDonationType();
                donateStatus = DonateStatusEnum.GIFT.getEnumValue();
                botUtils.sendReplyKeyboard(chatId, reportType, fileTypeMessage);
            }

            /**
             * in case user choose to write current donation to new report
             * */
            else if (messageFromClint.equals("new report")) {

                isNewReport = true;

                if (prossesEscorter.donationTypeCompleted) {
                    botUtils.sendTextMessage(chatId, newReportRequestMessage);
                    prossesEscorter.completeFileReportType();
                } else {
                    botUtils.sendTextMessage(chatId, "don't skip steps please");
                }
            }
            /**
             * in case user choose to write donation to existing report
             * */
            else if (messageFromClint.equals("existing report")) {

                isNewReport = false;

                if (prossesEscorter.donationTypeCompleted) {
                    prossesEscorter.completeFileReportType();
                    sendReplayKeyBordToUser(chatId, reportsManager.getAllReportsNames(), "please select report");
                } else {
                    botUtils.sendTextMessage(chatId, "don't skip steps please");
                }

                /**
                 * report handler
                 * */
            } else if (messageFromClint.startsWith("report")) {

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
            }

            /**
             * in case user choose to produce previous report
             * bot will send all report names
             * user will select from ReplayKeyBord
             * */
            else if (messageFromClint.equals("get report by name")) {

                botUtils.sendTextMessage(chatId, "please choose your report");
                sendReplayKeyBordToUser(chatId, reportsManager.getAllReportsNames(), "please select report");
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
                    sendMessageToUser(chatId, "don't skip steps please");
                }
            }
        }

    }

    private void initVariablesAndSendEndingMessage(Long chatId) {
        sendMessageToUser(chatId, "thank you the donation saved in " + report.getName()); // todo add emoji or picture for gritting
        prossesEscorter = new ProssesEscorter();
        String donateStatus = null;
        isNewReport = false;
        isGetReport = false;
        report = null;

    }

    private void requestForDonation(Long chatId) {
        botUtils.sendTextMessage(chatId, "please send the donors with '-' " +
                "between name and amount of donation");
    }

    private void requestForReportType(Long chatId) {

        List<String> reportType = new ArrayList<>();
        reportType.addAll(Arrays.asList("new report", "existing report"));
        botUtils.sendReplyKeyboard(chatId, reportType, "and the report will be written to?...");
    }

    private void complainAboutSkippingProsses(Long chatId) {
        botUtils.sendTextMessage(chatId, "you need to choose donation type first");
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
        sendDocument.setCaption("there you go");
        sendDocument(sendDocument);
    }

    public String getBotUsername() {
        return "meronBot";
    }

    public String getBotToken() {
        return "926814310:AAEjdF_lCjsFTVBZXfhFyoiPnUxrtE-HfZE";
    }
}
