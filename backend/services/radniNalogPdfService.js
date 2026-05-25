const fs = require('fs');
const path = require('path');
const PDFDocument = require('pdfkit');
const db = require('../models');

class RadniNalogPdfService {
  constructor() {
    this.page = {
      left: 52,
      right: 52,
      top: 48,
      bottom: 48
    };
    this.fontPath = this.findUnicodeFontPath();
  }

  findUnicodeFontPath() {
    const candidates = [
      path.resolve(__dirname, '../fonts/DejaVuSans.ttf'),
      '/home/atesttea/realapi/fonts/Arial Unicode.ttf',      
      '/Library/Fonts/Arial Unicode.ttf',
      '/System/Library/Fonts/Supplemental/Arial Unicode.ttf',
      '/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf',
      '/usr/share/fonts/truetype/liberation/LiberationSans-Regular.ttf',
      '/usr/share/fonts/truetype/freefont/FreeSans.ttf'
    ];

    return candidates.find((candidate) => fs.existsSync(candidate)) || null;
  }

  getFontKey(variant = 'regular') {
    if (this.fontPath) {
      return this.fontPath;
    }

    if (!this.fontMissingWarned) {
      console.warn('PDF Unicode font not found. Falling back to standard PDF fonts, which may not render Croatian diacritics correctly.');
      this.fontMissingWarned = true;
    }

    if (variant === 'bold') {
      return 'Helvetica-Bold';
    }
    if (variant === 'italic') {
      return 'Helvetica-Oblique';
    }
    return 'Helvetica';
  }

  setFont(doc, variant = 'regular') {
    return doc.font(this.getFontKey(variant));
  }

  drawSectionHeader(doc, label, options = {}) {
    const fontSize = options.fontSize || 12;
    const afterSpace = options.afterSpace !== undefined ? options.afterSpace : 4;

    this.setFont(doc, options.bold ? 'bold' : 'regular').fontSize(fontSize).fillColor('#111111').text(label, this.page.left, doc.y, {
      width: doc.page.width - this.page.left - this.page.right,
      align: options.align || 'left'
    });

    doc.y += afterSpace;
  }

  getAvailableSpace(doc) {
    return doc.page.height - doc.y - this.page.bottom;
  }

  addNewPageWithHeader(doc) {
    doc.addPage();
    doc.y = this.page.top;
    
    // Add a subtle page header on subsequent pages
    this.setFont(doc, 'italic').fontSize(9).fillColor('#999999').text('ATEST TEAM - RADNI NALOG', this.page.left, this.page.top - 15, {
      width: doc.page.width - this.page.left - this.page.right,
      align: 'right'
    });
    
    doc.y = this.page.top;
  }

  ensureSpace(doc, requiredSpace = 80) {
    if (this.getAvailableSpace(doc) < requiredSpace) {
      this.addNewPageWithHeader(doc);
    }
  }

  formatDate(dateValue) {
    if (!dateValue) {
      return '';
    }

    const date = new Date(dateValue);
    if (Number.isNaN(date.getTime())) {
      return '';
    }

    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();
    return `${day}.${month}.${year}.`;
  }

  formatBoolean(value) {
    return value ? 'DA' : 'NE';
  }

  normalizeAktivnosti(aktivnosti) {
    if (!aktivnosti) {
      return [];
    }

    if (Array.isArray(aktivnosti)) {
      return aktivnosti
        .filter((item) => typeof item === 'string' && item.trim().length > 0)
        .map((item) => item.trim());
    }

    if (typeof aktivnosti === 'string') {
      try {
        const parsed = JSON.parse(aktivnosti);
        if (Array.isArray(parsed)) {
          return parsed
            .filter((item) => typeof item === 'string' && item.trim().length > 0)
            .map((item) => item.trim());
        }
      } catch (error) {
      }

      return aktivnosti.trim().length > 0 ? [aktivnosti.trim()] : [];
    }

    return [];
  }

  async resolveAktivnosti(aktivnosti) {
    if (!aktivnosti) {
      return [];
    }

    let aktivnostiArr = [];
    if (Array.isArray(aktivnosti)) {
      aktivnostiArr = aktivnosti;
    } else if (typeof aktivnosti === 'string') {
      try {
        const parsed = JSON.parse(aktivnosti);
        if (Array.isArray(parsed)) {
          aktivnostiArr = parsed;
        }
      } catch (error) {
        aktivnostiArr = [];
      }
    }

    // aktivnostiArr should now be array of IDs
    if (aktivnostiArr.length === 0) return [];
    const uniqueIds = [...new Set(aktivnostiArr.map(Number).filter(id => Number.isInteger(id) && id > 0))];
    if (uniqueIds.length === 0) return [];
    const aktivnostiConfig = await db.aktivnost.findAll({
      where: { id: uniqueIds },
      attributes: ['id', 'aktivnost']
    });
    const idToName = {};
    aktivnostiConfig.forEach(a => { idToName[a.id] = a.aktivnost; });
    return uniqueIds.map(id => idToName[id] || id);
  }

  buildFileName(nalog) {
    const brojNaloga = nalog.brojNaloga || `nalog-${nalog.id}`;
    const safeName = brojNaloga.replace(/[^a-zA-Z0-9-_]/g, '_');
    return `${safeName}.pdf`;
  }

  formatValue(value) {
    if (value === null || value === undefined) {
      return '';
    }

    if (typeof value === 'string') {
      const trimmed = value.trim();
      return trimmed.length > 0 ? trimmed : '';
    }

    return String(value);
  }

  joinAddress(narucitelj) {
    if (!narucitelj) {
      return '';
    }

    const adresaParts = [narucitelj.adresa, narucitelj.postanskiBroj, narucitelj.mjesto]
      .map((part) => (typeof part === 'string' ? part.trim() : part))
      .filter((part) => part);

    const drzava = typeof narucitelj.drzava === 'string' ? narucitelj.drzava.trim() : narucitelj.drzava;
    if (drzava) {
      adresaParts.push(drzava);
    }

    if (adresaParts.length === 0) {
      return '';
    }

    return adresaParts.join(', ');
  }

  drawTopHeader(doc) {
    const contentWidth = doc.page.width - this.page.left - this.page.right;

    this.setFont(doc, 'bold').fontSize(28).fillColor('#101010').text('ATEST TEAM', this.page.left, this.page.top, {
      width: 220,
      align: 'left'
    });

    this.setFont(doc).fontSize(8).fillColor('#101010').text('ZAŠTITA NA RADU, ZAŠTITA OD POŽARA, TEHNIČKO SAVJETOVANJE I MINIMALNI TEHNIČKI UVJETI', this.page.left, this.page.top + 33, {
      width: 300,
      align: 'left'
    });

    const ruleY = this.page.top + 63;
    doc.moveTo(this.page.left, ruleY).lineTo(this.page.left + contentWidth, ruleY).lineWidth(1.1).stroke('#111111');

    const titleBoxY = ruleY + 3;
    doc.rect(this.page.left, titleBoxY, contentWidth, 34).lineWidth(0.9).fillAndStroke('#E8E8E8', '#111111');

    this.setFont(doc, 'bold').fontSize(19).fillColor('#111111').text('RADNI NALOG', this.page.left, titleBoxY + 9, {
      width: contentWidth,
      align: 'center'
    });

    doc.y = titleBoxY + 49;
  }

  drawLabeledRow(doc, label, value, options = {}) {
    const labelWidth = options.labelWidth || 165;
    const valueX = this.page.left + labelWidth + 15;
    const valueWidth = doc.page.width - this.page.right - valueX;
    const startY = doc.y;

    this.setFont(doc, 'italic').fontSize(options.labelFontSize || 11).fillColor('#111111').text(`${label}:`, this.page.left, startY, {
      width: labelWidth,
      align: 'left'
    });

    this.setFont(doc, options.valueBold ? 'bold' : 'regular').fontSize(options.valueFontSize || 12).fillColor('#111111').text(this.formatValue(value), valueX, startY, {
      width: valueWidth,
      align: 'left'
    });

    const rowBottom = Math.max(doc.y, startY + 16);
    doc.y = rowBottom + (options.afterSpace || 5);
  }

  drawSplitContactRow(doc, leftLabel, leftValue, rightLabel, rightValue) {
    const labelWidth = 60;
    const leftX = this.page.left + 180;
    const rightX = this.page.left + 395;
    const startY = doc.y;

    this.setFont(doc, 'italic').fontSize(11).text(`${leftLabel}:`, leftX, startY, { width: labelWidth, align: 'left' });
    this.setFont(doc).fontSize(12).text(this.formatValue(leftValue), leftX + labelWidth + 8, startY, {
      width: rightX - leftX - labelWidth - 15,
      align: 'left'
    });

    this.setFont(doc, 'italic').fontSize(11).text(`${rightLabel}:`, rightX, startY, { width: labelWidth, align: 'left' });
    this.setFont(doc).fontSize(12).text(this.formatValue(rightValue), rightX + labelWidth + 8, startY, {
      width: doc.page.width - this.page.right - (rightX + labelWidth + 8),
      align: 'left'
    });

    doc.y = Math.max(doc.y, startY + 16) + 4;
  }

  drawSingleContactRow(doc, label, value) {
    const labelWidth = 60;
    const valueX = this.page.left + 180;
    const startY = doc.y;

    this.setFont(doc, 'italic').fontSize(11).text(`${label}:`, valueX, startY, {
      width: labelWidth,
      align: 'left'
    });

    this.setFont(doc).fontSize(12).text(this.formatValue(value), valueX + labelWidth + 8, startY, {
      width: doc.page.width - this.page.right - (valueX + labelWidth + 8),
      align: 'left'
    });

    doc.y = Math.max(doc.y, startY + 16) + 4;
  }

  drawNumberedAktivnosti(doc, aktivnosti) {
    const valueX = this.page.left + 180;
    const valueWidth = doc.page.width - this.page.right - valueX;

    if (aktivnosti.length === 0) {
      return;
    }

    aktivnosti.forEach((item, index) => {
      this.setFont(doc).fontSize(12).text(`${index + 1}. ${item}`, valueX, doc.y, {
        width: valueWidth,
        align: 'left'
      });
      doc.y += 2;
    });

    doc.y += 3;
  }

  async buildPdfBuffer(nalog) {
    const aktivnosti = await this.resolveAktivnosti(nalog.aktivnosti);
    const narucitelj = nalog.narucitelj || null;

    return new Promise((resolve, reject) => {
      const doc = new PDFDocument({
        margin: this.page.left,
        size: 'A4'
      });
      const chunks = [];

      doc.on('data', (chunk) => chunks.push(chunk));
      doc.on('end', () => resolve(Buffer.concat(chunks)));
      doc.on('error', reject);

      this.drawTopHeader(doc);

      this.drawLabeledRow(doc, 'Broj radnog naloga', nalog.brojNaloga, {
        valueBold: true,
        valueFontSize: 15,
        afterSpace: 8
      });

      this.ensureSpace(doc, 100);

      this.drawLabeledRow(doc, 'narucitelj', narucitelj ? narucitelj.name : '', {
        valueBold: true,
        valueFontSize: 16,
        afterSpace: 3
      });

      this.drawLabeledRow(doc, 'adresa narucitelja', this.joinAddress(narucitelj), {
        valueFontSize: 12,
        labelFontSize: 10,
        afterSpace: 3
      });

      this.drawLabeledRow(doc, 'Kontakt', narucitelj ? narucitelj.kontaktOsoba : '', {
        labelFontSize: 10,
        valueBold: true,
        valueFontSize: 12,
        afterSpace: 2
      });

      this.ensureSpace(doc, 80);

      this.drawSingleContactRow(doc, 'Telefon', narucitelj ? narucitelj.telefon : null);
      this.drawSingleContactRow(doc, 'Mobitel', narucitelj ? narucitelj.mobitel : null);
      this.drawSingleContactRow(doc, 'Fax', narucitelj ? narucitelj.fax : null);
      this.drawSingleContactRow(doc, 'E-mail', narucitelj ? narucitelj.email : null);

      this.drawLabeledRow(doc, 'OIB', narucitelj ? narucitelj.OIB : '', {
        afterSpace: 10
      });

      this.ensureSpace(doc, 100);

      this.drawLabeledRow(doc, 'Datum', this.formatDate(nalog.datum), {
        valueBold: true,
        afterSpace: 8
      });

      this.drawLabeledRow(doc, 'Objekt - Lokacija', nalog.objekt, {
        valueBold: true,
        afterSpace: 10
      });

      this.ensureSpace(doc, 120);

      this.drawSectionHeader(doc, 'Radne aktivnosti', {
        afterSpace: 2,
        bold: true
      });
      this.drawNumberedAktivnosti(doc, aktivnosti);

      this.ensureSpace(doc, 100);

      this.drawLabeledRow(doc, 'Komentari aktivnosti', nalog.opis, {
        valueFontSize: 12,
        afterSpace: 12
      });

      this.ensureSpace(doc, 80);

      this.drawLabeledRow(doc, 'Ostale aktivnosti', null, {
        afterSpace: 6
      });

      this.drawLabeledRow(doc, 'Djelatnici', null, {
        afterSpace: 6
      });

      this.ensureSpace(doc, 80);

      this.drawLabeledRow(doc, 'Ugovor', nalog.ugovor, {
        afterSpace: 4
      });
      this.drawLabeledRow(doc, 'Broj ponude', nalog.brojPonude, {
        afterSpace: 4
      });
      this.drawLabeledRow(doc, 'Narudžbenica', nalog.narudzbenica, {
        afterSpace: 4
      });
      this.drawLabeledRow(doc, 'broj racuna', nalog.brojRacuna, {
        afterSpace: 4
      });
      this.drawLabeledRow(doc, 'Napomena', narucitelj ? narucitelj.comment : null, {
        afterSpace: 2
      });

      doc.end();
    });
  }

  async generateRadniNalogPdf(radniNalogId) {
    const nalog = await db.radni_nalog.findByPk(radniNalogId, {
      include: [
        {
          model: db.narucitelj,
          as: 'narucitelj'
        }
      ]
    });

    if (!nalog) {
      throw new Error('Radni nalog not found');
    }

    const pdfBuffer = await this.buildPdfBuffer(nalog);

    return {
      fileName: this.buildFileName(nalog),
      contentType: 'application/pdf',
      buffer: pdfBuffer
    };
  }
}

module.exports = new RadniNalogPdfService();
