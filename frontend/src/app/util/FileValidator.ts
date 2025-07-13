export class FileValidator {

  private static readonly ALLOWED_TYPES = [
    'application/pdf',
    'application/msword',
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
    'application/vnd.ms-excel',
    'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    'application/vnd.ms-powerpoint',
    'application/vnd.openxmlformats-officedocument.presentationml.presentation',
    'image/png',
    'image/jpeg',
    'image/gif',
    'image/bmp',
    'application/epub+zip',
    'audio/mpeg',
    'video/mp4',
    'image/heic',
    'image/vnd.dwg',
    'application/vnd.google-earth.kmz'
  ];

  private static readonly TYPE_DESCRIPTIONS: { [index: string]: string } = {
    'application/pdf': 'PDF',
    'application/msword': 'DOC',
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document': 'DOCX',
    'application/vnd.ms-excel': 'XLS',
    'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet': 'XLSX',
    'application/vnd.ms-powerpoint': 'PPT',
    'application/vnd.openxmlformats-officedocument.presentationml.presentation': 'PPTX',
    'image/png': 'PNG',
    'image/jpeg': 'JPEG',
    'image/gif': 'GIF',
    'image/bmp': 'BMP',
    'application/epub+zip': 'EPUB',
    'audio/mpeg': 'MP3',
    'video/mp4': 'MP4',
    'image/heic': 'HEIC',
    'image/vnd.dwg': 'DWG',
    'application/vnd.google-earth.kmz': 'KMZ'
  };

  private static readonly MAX_SIZE_BYTES = 40 * 1024 * 1024; // 40MB
  private static readonly MAX_SIZE_BYTES_VIDEO = 5 * 1024 * 1024 * 1024; // 5GB

  /**
   * Valida se o tipo do arquivo é permitido
   * @param file - Arquivo a ser validado
   * @returns true se o tipo for permitido, false caso contrário
   */
  static isValidType(file: File): boolean {
    return this.ALLOWED_TYPES.includes(file.type);
  }

  /**
   * Valida se o tamanho do arquivo está dentro do limite
   * @param file - Arquivo a ser validado
   * @returns true se o tamanho estiver dentro do limite, false caso contrário
   */
  static isValidSize(file: File): boolean {
    // Arquivos MP4 podem ter até 5GB
    if (file.type === 'video/mp4') {
      return file.size <= this.MAX_SIZE_BYTES_VIDEO;
    }
    return file.size <= this.MAX_SIZE_BYTES;
  }

  /**
   * Valida arquivo completo (tipo e tamanho)
   * @param file - Arquivo a ser validado
   * @returns objeto com resultado da validação e mensagem de erro
   */
  static validateFile(file: File): { isValid: boolean; errorMessage?: string } {
    if (!this.isValidType(file)) {
      return {
        isValid: false,
        errorMessage: `Tipo de arquivo não permitido. Tipos aceitos: ${this.getAllowedTypesDescription()}`
      };
    }

    if (!this.isValidSize(file)) {
      const fileSizeMB = (file.size / 1024 / 1024).toFixed(2);
      return {
        isValid: false,
        errorMessage: `Arquivo muito grande. Tamanho máximo: 40MB. Tamanho atual: ${fileSizeMB}MB`
      };
    }

    return {isValid: true};
  }

  /**
   * Retorna a descrição amigável do tipo do arquivo
   * @param mimeType - Tipo MIME do arquivo
   * @returns descrição amigável ou o próprio tipo MIME
   */
  static getTypeDescription(mimeType: string): string {
    return this.TYPE_DESCRIPTIONS[mimeType] || mimeType;
  }

  /**
   * Retorna lista de tipos permitidos em formato amigável
   * @returns string com tipos permitidos separados por vírgula
   */
  static getAllowedTypesDescription(): string {
    return Object.values(this.TYPE_DESCRIPTIONS).join(', ');
  }

  /**
   * Retorna lista de tipos MIME permitidos
   * @returns array com tipos MIME permitidos
   */
  static getAllowedTypes(): string[] {
    return [...this.ALLOWED_TYPES];
  }

  /**
   * Formata o tamanho do arquivo em formato legível
   * @param bytes - Tamanho em bytes
   * @returns string formatada com tamanho
   */
  static formatFileSize(bytes: number): string {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  }
}
