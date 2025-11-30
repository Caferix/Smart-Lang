package com.scu.smartlang.presentation.ui.aichat;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.scu.smartlang.R;
import com.scu.smartlang.presentation.ui.aichat.adapter.ChatMessageAdapter;
import com.scu.smartlang.presentation.ui.aichat.model.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public class AiChatFragment extends Fragment {

    private RecyclerView rvChatMessages;
    private EditText etChatInput;
    private Button btnSend;
    private Chip chipSuggestion;
    private LinearLayout levelButtonsContainer;
    private LinearLayout programMaterialContainer;
    private ChatMessageAdapter chatMessageAdapter;
    private List<ChatMessage> chatMessages;
    private String selectedLevel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ai_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvChatMessages = view.findViewById(R.id.rv_chat_messages);
        etChatInput = view.findViewById(R.id.et_chat_input);
        btnSend = view.findViewById(R.id.btn_send);
        chipSuggestion = view.findViewById(R.id.chip_suggestion);
        levelButtonsContainer = view.findViewById(R.id.level_buttons_container);
        programMaterialContainer = view.findViewById(R.id.program_material_container);

        chatMessages = new ArrayList<>();
        chatMessageAdapter = new ChatMessageAdapter(chatMessages);
        rvChatMessages.setLayoutManager(new LinearLayoutManager(getContext()));
        rvChatMessages.setAdapter(chatMessageAdapter);

        addInitialMessage();

        btnSend.setOnClickListener(v -> sendMessage());

        chipSuggestion.setOnClickListener(v -> {
            etChatInput.setText(chipSuggestion.getText());
            sendMessage();
        });

        View.OnClickListener levelClickListener = v -> {
            Button button = (Button) v;
            selectedLevel = button.getText().toString();
            onLevelSelected(selectedLevel);
        };

        view.findViewById(R.id.btn_a1).setOnClickListener(levelClickListener);
        view.findViewById(R.id.btn_a2).setOnClickListener(levelClickListener);
        view.findViewById(R.id.btn_b1).setOnClickListener(levelClickListener);
        view.findViewById(R.id.btn_b2).setOnClickListener(levelClickListener);
        view.findViewById(R.id.btn_c1).setOnClickListener(levelClickListener);
        view.findViewById(R.id.btn_c2).setOnClickListener(levelClickListener);

        view.findViewById(R.id.btn_program).setOnClickListener(v -> onProgramOrMaterialSelected("Program istiyorum"));
        view.findViewById(R.id.btn_material).setOnClickListener(v -> onProgramOrMaterialSelected("Materyal istiyorum"));
    }

    private void addInitialMessage() {
        chatMessages.add(new ChatMessage("Merhaba seni görmek çok güzel! Nasıl yardımcı olabilirim", false));
        chatMessageAdapter.notifyItemInserted(chatMessages.size() - 1);
    }

    private void sendMessage() {
        String messageText = etChatInput.getText().toString().trim();
        if (!messageText.isEmpty()) {
            chatMessages.add(new ChatMessage(messageText, true));
            chatMessageAdapter.notifyItemInserted(chatMessages.size() - 1);
            etChatInput.setText("");
            rvChatMessages.scrollToPosition(chatMessages.size() - 1);

            if (messageText.equals("Bir program oluşturmak istiyorum")) {
                showLevelSelection();
            }
        }
    }

    private void showLevelSelection() {
        chatMessages.add(new ChatMessage("Tabii ki hangi seviye bir program istersiniz?", false));
        chatMessageAdapter.notifyItemInserted(chatMessages.size() - 1);
        rvChatMessages.scrollToPosition(chatMessages.size() - 1);
        levelButtonsContainer.setVisibility(View.VISIBLE);
        chipSuggestion.setVisibility(View.GONE);
    }

    private void onLevelSelected(String level) {
        chatMessages.add(new ChatMessage(level, true));
        chatMessageAdapter.notifyItemInserted(chatMessages.size() - 1);
        rvChatMessages.scrollToPosition(chatMessages.size() - 1);
        levelButtonsContainer.setVisibility(View.GONE);

        chatMessages.add(new ChatMessage("Program mı oluşturmamı istersin yoksa materyal mi önermemi istersin?", false));
        chatMessageAdapter.notifyItemInserted(chatMessages.size() - 1);
        rvChatMessages.scrollToPosition(chatMessages.size() - 1);
        programMaterialContainer.setVisibility(View.VISIBLE);
    }

    private void onProgramOrMaterialSelected(String selection) {
        chatMessages.add(new ChatMessage(selection, true));
        chatMessageAdapter.notifyItemInserted(chatMessages.size() - 1);
        rvChatMessages.scrollToPosition(chatMessages.size() - 1);
        programMaterialContainer.setVisibility(View.GONE);

        if ("A1".equals(selectedLevel)) {
            if ("Program istiyorum".equals(selection)) {
                String a1Program = "<b>Alan:</b> Kelime Bilgisi<br>" +
                        "<b>Odak Noktaları:</b> Çok temel kelimeler (sayılar, renkler, aile, yiyecekler, ev eşyaları, saat).<br>" +
                        "<b>Aktivite Önerileri:</b> Görsel kartlar, basit kelime eşleştirme oyunları.<br><br>" +
                        "<b>Alan:</b> Gramer<br>" +
                        "<b>Odak Noktaları:</b> Olmak (To Be) fiili, basit şimdiki zaman (Simple Present Tense), çoğullar, sahiplik (my, your), basit edatlar (in, on, at).<br>" +
                        "<b>Aktivite Önerileri:</b> Basit cümle kurma alıştırmaları, boşluk doldurma.<br><br>" +
                        "<b>Alan:</b> Okuma<br>" +
                        "<b>Odak Noktaları:</b> Çok kısa ve basit metinler, menüler, etiketler, kolay okuma kitapları (gradet readers).<br>" +
                        "<b>Aktivite Önerileri:</b> Kelimeleri daire içine alma, soruları cevaplama.<br><br>" +
                        "<b>Alan:</b> Dinleme<br>" +
                        "<b>Odak Noktaları:</b> Çok yavaş ve net konuşulan basit diyaloglar, kısa talimatlar.<br>" +
                        "<b>Aktivite Önerileri:</b> Dinle ve tekrar et, resimle eşleştirme.<br><br>" +
                        "<b>Alan:</b> Konuşma<br>" +
                        "<b>Odak Noktaları:</b> Kendini tanıtma, selamlaşma, yaş/milliyet sorma/söyleme, basit alışveriş diyalogları.<br>" +
                        "<b>Aktivite Önerileri:</b> Rol yapma (selamlaşma, sipariş verme), tekrar etme.";
                chatMessages.add(new ChatMessage(a1Program, false));
                chatMessages.add(new ChatMessage("Ayrıca uygulamamız üzerinden egzersiz yapmayı unutma!", false));
            } else if ("Materyal istiyorum".equals(selection)) {
                String a1Material = "<b>Materyal Türü:</b> Ders Kitabı Serisi<br>" +
                        "<b>Öneri ve Amaç:</b> \"English File Beginner\" veya \"Cutting Edge Starter\". (Gramer ve temel kelime dağarcığını görsel destekle sunar.)<br><br>" +
                        "<b>Materyal Türü:</b> Uygulama<br>" +
                        "<b>Öneri ve Amaç:</b> Duolingo veya Memrise. (Günlük, oyunlaştırılmış ve temel kelime ezberlemeye odaklı.)<br><br>" +
                        "<b>Materyal Türü:</b> Okuma<br>" +
                        "<b>Öneri ve Amaç:</b> \"Graded Readers\" serilerinin A1 seviyesi (örneğin Oxford Bookworms Starter). (Çok basit cümlelerle yazılmış kısa hikayeler.)<br><br>" +
                        "<b>Materyal Türü:</b> Video/Dinleme<br>" +
                        "<b>Öneri ve Amaç:</b> BBC Learning English'in \"The English We Speak\" serisinin çok yavaş bölümleri veya YouTube'da \"Kids English\" kanalları.<br><br>" +
                        "<b>Materyal Türü:</b> Sözlük<br>" +
                        "<b>Öneri ve Amaç:</b> Bol resimli ve basit tanımlı görsel sözlükler.";
                chatMessages.add(new ChatMessage(a1Material, false));
            }
        } else if ("A2".equals(selectedLevel)) {
            if ("Program istiyorum".equals(selection)) {
                String a2Program = "<b>Alan:</b> Kelime Bilgisi<br>" +
                    "<b>Odak Noktaları:</b> Günlük rutinler, hava durumu, yol tarifleri, hobiler, basit sıfatlar ve zarflar.<br>" +
                    "<b>Aktivite Önerileri:</b> Günlük tutma (çok basit İngilizce ile), tematik listeler oluşturma.<br><br>" +
                    "<b>Alan:</b> Gramer<br>" +
                    "<b>Odak Noktaları:</b> Geçmiş zaman (Simple Past Tense), Gelecek zaman (Going to), kiplilik fiilleri (can, must), karşılaştırmalar (-er, more).<br>" +
                    "<b>Aktivite Önerileri:</b> Hikaye tamamlama, geçmiş deneyimler hakkında konuşma.<br><br>" +
                    "<b>Alan:</b> Okuma<br>" +
                    "<b>Odak Noktaları:</b> Kısa mektuplar/e-postalar, basit hikayeler, ilanlar.<br>" +
                    "<b>Aktivite Önerileri:</b> Ana fikri bulma, doğru/yanlış sorularını cevaplama.<br><br>" +
                    "<b>Alan:</b> Dinleme<br>" +
                    "<b>Odak Noktaları:</b> Standart hızda konuşulan kısa sohbetler, telefon mesajları.<br>" +
                    "<b>Aktivite Önerileri:</b> Dinlediğini not alma, eksik kelimeleri tamamlama.<br><br>" +
                    "<b>Alan:</b> Konuşma<br>" +
                    "<b>Odak Noktaları:</b> Günlük rutinleri anlatma, yol tarifi verme/sorma, bir şeyi önerme, onaylama/reddetme.<br>" +
                    "<b>Aktivite Önerileri:</b> Günlük rutinini anlatan kısa bir sunum yapma.";
                chatMessages.add(new ChatMessage(a2Program, false));
                chatMessages.add(new ChatMessage("Ayrıca uygulamamız üzerinden egzersiz yapmayı unutma!", false));
            } else if ("Materyal istiyorum".equals(selection)) {
                String a2Material = "<b>Materyal Türü:</b> Ders Kitabı Serisi<br>" +
                    "<b>Öneri ve Amaç:</b> \"English File Elementary\" veya \"New Headway Elementary\". (Geçmiş zamanı ve günlük rutinleri öğrenmeye odaklı.)<br><br>" +
                    "<b>Materyal Türü:</b> Uygulama<br>" +
                    "<b>Öneri ve Amaç:</b> Quizlet (Kendi kelime setlerini oluşturmak ve basit cümle yapılarını pekiştirmek için.)<br><br>" +
                    "<b>Materyal Türü:</b> Okuma<br>" +
                    "<b>Öneri ve Amaç:</b> \"Graded Readers\" A2 seviyesi. (Kısa e-postalar, basit haber metinleri ve yol tarifleri içeren metinler.)<br><br>" +
                    "<b>Materyal Türü:</b> Video/Dinleme<br>" +
                    "<b>Öneri ve Amaç:</b> VOA Learning English'in \"Level 1\" hikayeleri. (Yavaş ve anlaşılır konuşma hızı.)<br><br>" +
                    "<b>Materyal Türü:</b> Gramer Kaynağı<br>" +
                    "<b>Öneri ve Amaç:</b> \"English Grammar in Use\" (Essential). (Temel gramer konularını bol alıştırmayla pekiştirir.)";
                chatMessages.add(new ChatMessage(a2Material, false));
            }
        } else if ("B1".equals(selectedLevel)) {
            if ("Program istiyorum".equals(selection)) {
                String b1Program = "<b>Alan:</b> Kelime Bilgisi<br>" +
                    "<b>Odak Noktaları:</b> Duygular, görüşler, seyahat, iş, teknoloji, yaygın deyimsel fiiller (phrasal verbs).<br>" +
                    "<b>Aktivite Önerileri:</b> İngilizce bloglar/makaleler okuma, kelime ailelerini öğrenme.<br><br>" +
                    "<b>Alan:</b> Gramer<br>" +
                    "<b>Odak Noktaları:</b> Şimdiki/Geçmiş Sürekli Zamanlar (Continuous Tenses), Zaman Bağlaçları (when, before, after), Edilgen Yapı (Passive Voice) (temel), Koşul Cümleleri (If Clauses) (Tip 1).<br>" +
                    "<b>Aktivite Önerileri:</b> Deneme (paragraf) yazma, bir filmi özetleme.<br><br>" +
                    "<b>Alan:</b> Okuma<br>" +
                    "<b>Odak Noktaları:</b> Gazete/dergi makaleleri (basitleştirilmiş), kişisel mektuplar, kısa romanlar.<br>" +
                    "<b>Aktivite Önerileri:</b> Okuduğunu kendi kelimeleriyle özetleme, yazarın amacını anlama.<br><br>" +
                    "<b>Alan:</b> Dinleme<br>" +
                    "<b>Odak Noktaları:</b> Standart hızda ve net konuşulan radyo programları, röportajlar, filmlerin ana hatları.<br>" +
                    "<b>Aktivite Önerileri:</b> Uzun konuşmaları dinlerken ana noktaları belirleme.<br><br>" +
                    "<b>Alan:</b> Konuşma<br>" +
                    "<b>Odak Noktaları:</b> Bir fikri açıklama ve gerekçelendirme, deneyimleri, umutları ve hedefleri anlatma.<br>" +
                    "<b>Aktivite Önerileri:</b> Tartışmalara katılma (basit konularda), bir olayı detaylı anlatma.";
                chatMessages.add(new ChatMessage(b1Program, false));
                chatMessages.add(new ChatMessage("Ayrıca uygulamamız üzerinden egzersiz yapmayı unutma!", false));
            } else if ("Materyal istiyorum".equals(selection)) {
                String b1Material = "<b>Materyal Türü:</b> Ders Kitabı Serisi<br>" +
                    "<b>Öneri ve Amaç:</b> \"English File Pre-Intermediate/Intermediate\" veya \"Cutting Edge Pre-Intermediate\". (Daha karmaşık zamanlar ve deyimsel fiillerin girişi.)<br><br>" +
                    "<b>Materyal Türü:</b> Uygulama<br>" +
                    "<b>Öneri ve Amaç:</b> Anki (Öğrencinin zorlandığı kelimeleri tekrar ettiren aralıklı tekrarlama sistemi için.)<br><br>" +
                    "<b>Materyal Türü:</b> Okuma<br>" +
                    "<b>Öneri ve Amaç:</b> \"Graded Readers\" B1 seviyesi ve İngilizce Wikipedia’nın \"Simple English\" versiyonundaki makaleler.<br><br>" +
                    "<b>Materyal Türü:</b> Video/Dinleme<br>" +
                    "<b>Öneri ve Amaç:</b> BBC Learning English’in alt başlıkları olan kısa haber videoları ve YouTube’daki eğitici \"TED Talks\" (alt yazılı).<br><br>" +
                    "<b>Materyal Türü:</b> Yazma Kaynağı<br>" +
                    "<b>Öneri ve Amaç:</b> Basit blog yazıları veya günlük tutma alıştırmaları için Evernote veya benzeri bir not alma uygulaması.";
                chatMessages.add(new ChatMessage(b1Material, false));
            }
        } else if ("B2".equals(selectedLevel)) {
            if ("Program istiyorum".equals(selection)) {
                String b2Program = "<b>Alan:</b> Kelime Bilgisi<br>" +
                    "<b>Odak Noktaları:</b> Soyut konular (politika, çevre, bilim), karmaşık deyimsel fiiller, eş anlamlılar (synonyms), zıt anlamlılar (antonyms).<br>" +
                    "<b>Aktivite Önerileri:</b> İngilizce podcast'ler dinleme, hedef kelimeleri kullanarak eleştirel inceleme yazma.<br><br>" +
                    "<b>Alan:</b> Gramer<br>" +
                    "<b>Odak Noktaları:</b> Mükemmel Zamanlar (Perfect Tenses - Present/Past), Edilgen Yapı (Passive Voice) (ileri), Dolaylı Anlatım (Reported Speech), Koşul Cümleleri (If Clauses) (Tip 2 ve 3).<br>" +
                    "<b>Aktivite Önerileri:</b> Daha uzun ve resmi denemeler yazma, giriş/gelişme/sonuç yapısını kullanma.<br><br>" +
                    "<b>Alan:</b> Okuma<br>" +
                    "<b>Odak Noktaları:</b> Orijinal gazete/dergi makaleleri, uzmanlık alanı ile ilgili raporlar, edebiyat.<br>" +
                    "<b>Aktivite Önerileri:</b> Detaylı okuma, yazarın tonunu ve bakış açısını analiz etme.<br><br>" +
                    "<b>Alan:</b> Dinleme<br>" +
                    "<b>Odak Noktaları:</b> Uzun konuşmalar, TV haberleri, filmler, tartışmalar.<br>" +
                    "<b>Aktivite Önerileri:</b> Hızlı ve karmaşık konuşmaları anlama ve not alma.<br><br>" +
                    "<b>Alan:</b> Konuşma<br>" +
                    "<b>Odak Noktaları:</b> Bir konuda detaylı görüş bildirme, argümanları destekleme, farklı seçeneklerin avantaj/dezavantajlarını sunma.<br>" +
                    "<b>Aktivite Önerileri:</b> Grup tartışmalarına liderlik etme, fikirlerini akıcı ve doğal bir şekilde ifade etme.";
                chatMessages.add(new ChatMessage(b2Program, false));
                chatMessages.add(new ChatMessage("Ayrıca uygulamamız üzerinden egzersiz yapmayı unutma!", false));
            } else if ("Materyal istiyorum".equals(selection)) {
                String b2Material = "<b>Materyal Türü:</b> Ders Kitabı Serisi<br>" +
                    "<b>Öneri ve Amaç:</b> \"English File Upper-Intermediate\" veya \"Face2Face Upper-Intermediate\". (Akıcı konuşma ve akademik dilin temelleri.)<br><br>" +
                    "<b>Materyal Türü:</b> Kelime/Deyim<br>" +
                    "<b>Öneri ve Amaç:</b> \"English Collocations in Use (Intermediate)\". (Kelimelerin bir arada nasıl kullanıldığını öğrenmek için kritik.)<br><br>" +
                    "<b>Materyal Türü:</b> Okuma<br>" +
                    "<b>Öneri ve Amaç:</b> Orijinal kısa hikayeler (Örn: Roald Dahl), İngilizce gazete makaleleri (Örn: The Guardian/BBC News).<br><br>" +
                    "<b>Materyal Türü:</b> Video/Dinleme<br>" +
                    "<b>Öneri ve Amaç:</b> Orijinal TED Talks (altyazısız denemeler), İngilizce film/dizi (altyazısız). Podcast'ler (Örn: All Ears English).<br><br>" +
                    "<b>Materyal Türü:</b> Gramer Kaynağı<br>" +
                    "<b>Öneri ve Amaç:</b> \"Advanced Grammar in Use\" (Daha karmaşık gramer yapılarını derinlemesine öğrenmek için.)";
                chatMessages.add(new ChatMessage(b2Material, false));
            }
        } else if ("C1".equals(selectedLevel)) {
            if ("Program istiyorum".equals(selection)) {
                String c1Program = "<b>Alan:</b> Kelime Bilgisi<br>" +
                    "<b>Odak Noktaları:</b> Akademik kelimeler, resmi dil, atasözleri, deyimler (idioms), kollokasyonlar (collocations).<br>" +
                    "<b>Aktivite Önerileri:</b> Orijinal akademik metinler okuma, hedef kelimeleri kullanarak karmaşık bir konu hakkında makale yazma.<br><br>" +
                    "<b>Alan:</b> Gramer<br>" +
                    "<b>Odak Noktaları:</b> Cümle bağlama yöntemleri, ters çevirme (inversion), karmaşık edat öbekleri, zaman uyumu (sequence of tenses).<br>" +
                    "<b>Aktivite Önerileri:</b> Akademik makaleleri eleştirel bir dille özetleme ve değerlendirme.<br><br>" +
                    "<b>Alan:</b> Okuma<br>" +
                    "<b>Odak Noktaları:</b> Zor ve uzun edebi metinler, bilimsel makaleler, sözleşmeler.<br>" +
                    "<b>Aktivite Önerileri:</b> Alt metinleri ve imaları anlama, metinler arası bağlantı kurma.<br><br>" +
                    "<b>Alan:</b> Dinleme<br>" +
                    "<b>Odak Noktaları:</b> Yüksek hızda, aksanlı ve uzun sunumlar, TV şovları, film/dizi.<br>" +
                    "<b>Aktivite Önerileri:</b> Altyazısız dizi/film izleme, ana konuşmacının duygusal tonunu anlama.<br><br>" +
                    "<b>Alan:</b> Konuşma<br>" +
                    "<b>Odak Noktaları:</b> Karmaşık konuları yapılandırılmış, detaylı bir şekilde sunma, mizahi ve imaları kullanma.<br>" +
                    "<b>Aktivite Önerileri:</b> Resmi toplantılarda veya konferanslarda aktif rol alma.";
                chatMessages.add(new ChatMessage(c1Program, false));
                chatMessages.add(new ChatMessage("Ayrıca uygulamamız üzerinden egzersiz yapmayı unutma!", false));
            } else if ("Materyal istiyorum".equals(selection)) {
                String c1Material = "<b>Materyal Türü:</b> Ders Kitabı Serisi<br>" +
                    "<b>Öneri ve Amaç:</b> \"New Headway Advanced\" veya \"Proficiency Masterclass\" (Cambridge). (Akademik ve resmi dilin incelikleri.)<br><br>" +
                    "<b>Materyal Türü:</b> Kelime/Deyim<br>" +
                    "<b>Öneri ve Amaç:</b> \"English Idioms in Use (Advanced)\" ve \"Academic Vocabulary in Use\". (Doğal konuşma ve akademik yazım için.)<br><br>" +
                    "<b>Materyal Türü:</b> Okuma<br>" +
                    "<b>Öneri ve Amaç:</b> Zorlayıcı edebi romanlar (Örn: George Orwell), İngilizce dergiler (Örn: The Economist).<br><br>" +
                    "<b>Materyal Türü:</b> Video/Dinleme<br>" +
                    "<b>Öneri ve Amaç:</b> Hızlı konuşulan ve spesifik konuları içeren uzman podcast'ler (Bilim, tarih, felsefe vb.). Haber tartışma programları.<br><br>" +
                    "<b>Materyal Türü:</b> Yazma Kaynağı<br>" +
                    "<b>Öneri ve Amaç:</b> Grammarly veya benzeri gelişmiş yazım denetleme araçları. (Doğruluk ve stil kontrolü için.)";
                chatMessages.add(new ChatMessage(c1Material, false));
            }
        } else if ("C2".equals(selectedLevel)) {
            if ("Program istiyorum".equals(selection)) {
                String c2Program = "<b>Alan:</b> Kelime Bilgisi<br>" +
                    "<b>Odak Noktaları:</b> Çok nadir kelimeler, jargon, kültür odaklı deyimler, yüksek seviye retorik dil.<br>" +
                    "<b>Aktivite Önerileri:</b> Edebi eleştiriler okuma, İngilizce'de şiir veya yaratıcı yazı denemeleri.<br><br>" +
                    "<b>Alan:</b> Gramer<br>" +
                    "<b>Odak Noktaları:</b> Tam bir ustalık, çok ince nüansları ve stilistik varyasyonları anlama ve kullanma.<br>" +
                    "<b>Aktivite Önerileri:</b> Profesyonel çeviri alıştırmaları, farklı stillerde (resmi, samimi, akademik) metinler yazma.<br><br>" +
                    "<b>Alan:</b> Okuma<br>" +
                    "<b>Odak Noktaları:</b> Tarihi belgeler, felsefi metinler, karmaşık edebi eserler.<br>" +
                    "<b>Aktivite Önerileri:</b> Metnin dilbilimsel ve kültürel bağlamını analiz etme.<br><br>" +
                    "<b>Alan:</b> Dinleme<br>" +
                    "<b>Odak Noktaları:</b> Çok hızlı konuşulan ve karmaşık aksanlar içeren konuşmaları tam olarak anlama.<br>" +
                    "<b>Aktivite Önerileri:</b> Uluslararası konferans kayıtlarını dinleme, podcast'lere katılım.<br><br>" +
                    "<b>Alan:</b> Konuşma<br>" +
                    "<b>Odak Noktaları:</b> Her türlü duruma uygun, spontane, akıcı ve doğal bir dil kullanma, konuşmada hata yapmama.<br>" +
                    "<b>Aktivite Önerileri:</b> Anadili İngilizce olan kişilerle herhangi bir konuda tartışma, röportaj yapma.";
                chatMessages.add(new ChatMessage(c2Program, false));
                chatMessages.add(new ChatMessage("Ayrıca uygulamamız üzerinden egzersiz yapmayı unutma!", false));
            } else if ("Materyal istiyorum".equals(selection)) {
                String c2Material = "<b>Materyal Türü:</b> Ders Kitabı Serisi<br>" +
                    "<b>Öneri ve Amaç:</b> \"Cambridge English Proficiency (CPE)\" hazırlık kitapları. (Sınav yapısına ve en zor dil kullanımına odaklı.)<br><br>" +
                    "<b>Materyal Türü:</b> Kelime/Deyim<br>" +
                    "<b>Öneri ve Amaç:</b> \"The Oxford Dictionary of English\" veya \"Merriam-Webster Advanced Learner's Dictionary\". (Nadir kelimeler ve nüansları öğrenmek için.)<br><br>" +
                    "<b>Materyal Türü:</b> Okuma<br>" +
                    "<b>Öneri ve Amaç:</b> Klasik İngiliz/Amerikan edebiyatı (Shakespeare, Dickens, Woolf), çok teknik veya felsefi makaleler.<br><br>" +
                    "<b>Materyal Türü:</b> Video/Dinleme<br>" +
                    "<b>Öneri ve Amaç:</b> Üniversite dersleri (Yale/MIT Open Courseware), farklı İngilizce aksanları içeren zorlayıcı medya içerikleri.<br><br>" +
                    "<b>Materyal Türü:</b> Pratik<br>" +
                    "<b>Öneri ve Amaç:</b> Anadili İngilizce olan kişilerle dil değişimi (language exchange) veya resmi tartışma kulüplerine katılım.";
                chatMessages.add(new ChatMessage(c2Material, false));
            }
        } else {
            chatMessages.add(new ChatMessage("İstediğiniz içerik yakında eklenecektir.", false));
        }

        chatMessageAdapter.notifyItemInserted(chatMessages.size() - 1);
        rvChatMessages.scrollToPosition(chatMessages.size() - 1);
    }
}
