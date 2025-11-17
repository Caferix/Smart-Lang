# 🎮 Smart-Lang – Game Design (Eren & Erdem)

Bu doküman, Smart-Lang uygulamasındaki *oyunların mantığını, veri modellerini ve ekip sorumluluklarını* tanımlar.  
Bu bölümden *Eren Sezer & Erdem Taşar* sorumludur.

---

## 1. 🧩 Roller ve Sorumluluklar

### 👨‍💻 1.1. Biz (Eren & Erdem) – İçerik + Oyun Mantığı
- Hangi oyunların olacağına karar veririz.
- Oyun kurallarını, puanlamayı, level sistemini belirleriz.
- Kelime & cümle içeriklerini üretiriz (A1–A2 öncelikli).
- Java tarafında *WordQuestion, SentenceQuestion, MatchingQuestion* modellerini tasarlarız.
- UI ekibine “Bu ekran şu veriyi bekliyor” diye net kurallar veririz.

---

## 2. 🎮 Oyun Tipleri (İlk Sürüm Planı)

İlk versiyonda 3 oyun tipi vardır, ancak *sadece Eşleştirme Oyunu aktif çalışacak*.

---

### 🔵 2.1. Word Quiz – Çoktan Seçmeli Kelime Oyunu
(İlk sürümde pasif)

- 1 kelime gösterilir.
- 4 seçenek verilir → 1 doğru, 3 yanlış.
- Doğru: *+10 puan*
- Yanlış: *0 puan*
- Şıklar karışık şekilde gelir.

*Örnek:*  
Kelime: develop  
Şıklar: gelişmek (✔), hazırlamak, silmek, hatırlamak

---

### 🟣 2.2. Sentence Complete – Cümle Tamamlama
(İlk sürümde pasif)

- Cümlede boşluk bulunur.
- 4 seçenek verilir.
- Dil bilgisi veya anlam olarak doğru olan seçilir.

*Örnek:*  
I usually ___ coffee in the morning.  
✔ drink

---

### 🟢 2.3. Matching Game – Kelime–Anlam Eşleştirme
*(İlk sürümün ana oyunu — AKTİF)*

Amaç: İngilizce kelime → Türkçe anlam eşleştirmesi yaparak öğrenme pekiştirmek.

*Nasıl çalışır?*
- Ekran iki sütun:
  - Sol: İngilizce kelimeler (karışık)
  - Sağ: Türkçe anlamlar (karışık)
- Kullanıcı:
  1. Soldan kelimeye tıklar
  2. Sağdan doğru anlamı seçer
- Doğru eşleşirse:
  - Her iki kutu kilitlenir
  - *+1 puan*
- Yanlış eşleşmede:
  - Hata animasyonu
  - Puan değişmez

> Bu oyunda puan level ile değişmez.  
> *Her doğru +1 puan.*

---

## 3. 🧱 Veri Modelleri (Java)

Oyunların temelini oluşturan model sınıfları şunlardır:

---

### 📗 MatchingQuestion (aktif)

Alanlar:
- id
- level
- points = 1
- *correctPairs → Map<String, String>*
- englishWords → UI için karışık
- turkishWords → UI için karışık

Metodlar:
- checkMatch(en, tr) → doğru eşleşme mi?

---

## 4. 🧪 Geçici Test Verisi (Sample Data)

İlk sürümde backend veya database yok, bu yüzden oyunlar *Fake Data* ile çalışır.