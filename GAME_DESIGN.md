# 🎮 Smart-Lang – Game Design (Eren & Erdem)

Bu doküman, Smart-Lang uygulamasındaki *oyunların mantığını, veri formatını ve sorumlulukları* anlatır.  
Bu kısımdan *Eren Sezer & Erdem Taşar* sorumludur.

---

## 1. Roller ve Sorumluluklar

### 1.1. Biz (Eren & Erdem) – İçerik + Oyun Mantığı
- Oyun tiplerini tanımlar (hangi oyunlar var, nasıl oynanır?).
- Puanlama, level, streak, bonus gibi kuralları belirler.
- Kelime ve cümle içeriklerini hazırlar (A1, A2, B1 vs.).
- Gerekli *model sınıflarını ve veri yapısını* tarif eder, mümkünse kodlar.
- UI ekibine: “Bu ekran şu verileri bekliyor, şu fonksiyonları çağıracak” diye *net kontrat* verir.

### 1.2. UI Ekibi (Eren Kaya & Muhammed)
- Ekranların görünüşünü ve düzenini tasarlar (XML layout, tema, ikonlar).
- Butonlar, TextView’ler, progress barlar, animasyonlar vb.
- Bizim sağladığımız veri modellerini kullanarak ekranda gösterim yapar.
- Örnek: GameViewModel veya GameRepository'den gelen WordQuestion listesini ekranda çizer.

### 1.3. Diğer Ekiplerle İlişki
- *Firebase / Backend (Cafer)*: Kullanıcının skorlarının, ilerlemesinin, istatistiklerinin kaydı.
- *Local Database (Burak)*: Soruların/kelimelerin offline saklanması (ileride).
- *AI / API (Samet)*: İleride dinamik soru üretimi, öneri sistemi.

---

## 2. Oyun Tipleri

İlk aşamada 3 oyun tipi:

1. *Word Quiz* (çoktan seçmeli kelime)
2. *Sentence Complete* (cümle tamamlama)
3. *Matching Game* (eşleştirme oyunu)

### 2.1. Word Quiz (Çoktan Seçmeli Kelime Oyunu)

- Kullanıcıya *1 kelime* gösterilir (İngilizce).
- Altında *4 seçenek*:
    - 1 doğru Türkçe anlam
    - 3 yanlış ama mantıklı/benzer anlam
- Kullanıcı şıka tıklar:
    - ✅ Doğru → yeşil göster, +10 puan
    - ❌ Yanlış → kırmızı göster, doğru cevap gösterilir.

*Örnek (EN → TR):*

- Kelime: develop
- Şıklar:
    - gelişmek ✅
    - silmek ❌
    - hatırlamak ❌
    - hazırlamak ❌

*İleride seçenekler:*
- Zaman sınırı (ör: 10 sn).
- Zorluk seviyesi (A1, A2, B1).
- Streak (seri doğru cevap) bonusu.

---

### 2.2. Sentence Complete (Cümle Tamamlama)

- İçinde boşluk olan bir cümle gösterilir.
- Kullanıcıya 4 seçenek verilir.
- Amaç: anlam ve dil bilgisi olarak en doğru seçeneği seçmek.

*Örnek:*

- Cümle: I usually ___ coffee in the morning.
- Şıklar:
    - drink ✅
    - play ❌
    - read ❌
    - watch ❌

*İleride seçenekler:*
- Yanlış cevapta ipucu gösterme.
- Aynı kelimeyi farklı cümlelerde kullanma (pekiştirme).

---

### 2.3. Matching Game (Eşleştirme Oyunu)

Amaç: *Kelime–anlam eşleştirmesi* ile hem kelimeyi hem Türkçesini aynı ekranda görerek pekiştirmek.

- Ekranda 2 sütun:
    - Sol tarafta İngilizce kelimeler (karışık sıra).
    - Sağ tarafta Türkçe anlamlar (karışık sıra).
- Kullanıcı:
    - Önce soldan bir kelimeye tıklar.
    - Sonra sağdan doğru anlamı seçer.
- Eğer doğru çift seçtiyse:
    - İki kutu eşleşmiş olarak işaretlenir (renk değişir, kilitlenir).
    - *+1 puan* verilir.
- Yanlış eşleştirmede:
    - Kısa bir yanlış animasyonu / titreşim.
    - Puan değişmez (ceza yok).

> *Not:* Bu oyunda puan *levele göre değişmez*.  
> Her doğru eşleşme *sabit +1 puan*.  
> Ama kelimeler yine de A1 / A2 / B1 şeklinde seviyelendirilebilir (istatistik için).

*Veri yapısı (taslak):*

- MatchingPair
    - id: String
    - english: String
    - turkish: String
    - level: String (A1, A2…)
    - category: String (daily, travel…)

- MatchingGameRound
    - id: String
    - pairs: List<MatchingPair> (ör: 6 çift)
    - UI, bu listeden 2 sütun oluşturur (shuffle edilerek).

---

## 3. Veri Modeli (Kod Tarafı Taslak)

Java tarafında şuna benzer modeller kullanılacak:

### 3.1. Ortak Alanlar

Her soru / oyun elemanı için ortak fikir:

- id: String veya int
- level: String (A1, A2, B1…)
- category: String (daily, travel, business…)
- points: int (bu sorunun/çiftin puanı)
    - Word Quiz / Sentence: varsayılan 10
    - Matching Game: varsayılan 1

### 3.2. WordQuestion Modeli

- id: String
- word: String → gösterilecek kelime
- correctAnswer: String → doğru Türkçe anlam
- wrongOptions: List<String> → 3 yanlış ama mantıklı seçenek
- level: String (A1, A2…)
- category: String (ör: "daily", "school")

### 3.3. SentenceQuestion Modeli

- id: String
- sentence: String → boşluk içeren cümle (I usually ___ coffee...)
- correctAnswer: String → doğru kelime/ifade
- wrongOptions: List<String>
- level: String
- hint: String? (opsiyonel ipucu)

### 3.4. MatchingPair Modeli

- id: String
- english: String
- turkish: String
- level: String
- category: String

İlk aşamada veriler *koda gömülü (hard-coded)* olacak.  
Daha sonra:
- Room DB
- Firestore
- veya AI ile dinamik üretim seçeneklerine geçilebilir.

---

## 4. Puanlama ve İlerleme

### 4.1. Temel Puanlama

- *Word Quiz / Sentence Complete*
    - Doğru cevap: *+10 puan*
    - Yanlış: *0*
- *Matching Game*
    - Her doğru eşleşme: *+1 puan*
    - Yanlış eşleşme: 0 (ceza yok)

> Burada puan *level’e göre değişmez.*  
> Sadece soru tipi ve doğru/yanlış durumuna göre belirlenir.

### 4.2. Streak (Seri Doğrular) – (İlk versiyonda opsiyonel)

- Arka arkaya *3 doğru* → +5 bonus (Word/Sentence için).
- Matching game’de istenirse:
    - Arka arkaya 3 doğru eşleşmeye mini bonus verilebilir.
- Streak sistemi ilk versiyonda zorunlu değil, sonradan eklenebilir.

### 4.3. Level Mantığı (Basit Taslak)

- Toplam puan veya çözülmüş soru sayısına göre level progression:
    - A1 → A2 → B1
- Bu kısım ileride backend / local DB ile netleştirilecek.

---

## 5. UI – Kod İlişkisi

### 5.1. Bizim Sağlayacağımız Şeyler (API Tarzı)

- GameRepository veya benzeri bir sınıf:
    - List<WordQuestion> getWordQuestions(String level)
    - List<SentenceQuestion> getSentenceQuestions(String level)
    - List<MatchingPair> getMatchingPairs(String level)
- UI ekibi:
    - Bu fonksiyonları çağırır.
    - Gelen listeyi RecyclerView, grid, kartlar vb. ile gösterir.
    - Seçilen cevabı bize (ViewModel veya callback) bildirir.

### 5.2. UI Ekibinden Beklentiler

- Her oyun tipi için *ayrı ekran* veya ortak game ekranı:
    - Soru metni / kelime / grid
    - 4 seçenek butonu veya eşleştirme kutuları
    - Puan gösterimi
    - İlerleme göstergesi (kaçıncı soru / kaç eşleşme kaldı)

---

## 6. İlk Versiyon Planı

1. WordQuestion, SentenceQuestion ve MatchingPair Java sınıflarını yazacağız.
2. Test için:
    - En az *10 kelime* (Word Quiz),
    - En az *10 cümle* (Sentence),
    - En az *10 eşleşme çifti* (Matching)
      ekleyeceğiz (A1/A2 ağırlıklı).
3. GameRepository veya FakeGameDataSource içinde bu verileri liste olarak döndüreceğiz.
4. UI ekibi ile birlikte:
    - Word Quiz ekranı
    - Sentence Complete ekranı
    - Matching Game ekranı
      tasarlanacak ve bu modellerle bağlanacak.

> Bu doküman *oyun mantığı ve içerik ekibi (Eren & Erdem)* tarafından zamanla güncellenecektir.
