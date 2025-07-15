import {Component, ViewEncapsulation} from '@angular/core';
import {Filtro} from "./filtro.model";
import {DocumentoService} from "./documento.service";
import {Documento} from "./documento.model";
import {NgxExtendedPdfViewerService} from "ngx-extended-pdf-viewer";
import {MessageService} from "primeng/api";
import {FileValidator} from "./util/FileValidator";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  providers: [NgxExtendedPdfViewerService],
  encapsulation: ViewEncapsulation.Emulated
})
export class AppComponent {

  title = 'frontend';

  filtro: Filtro = {
    texto: '',
  }

  selectedUUID = "";
  termoBusca: string = '';
  documentos: Documento[] = [];
  showDialog: boolean = false;
  pdfbase64: string = "";


  constructor(private documentoService: DocumentoService,
              private ngxExtendedPdfViewerService: NgxExtendedPdfViewerService,
              private messageService: MessageService
  ) {
  }


  pesquisar() {

    console.log('Pesquisar por: ', this.filtro);

    this.documentoService.buscarPorConteudo(this.filtro).subscribe(response => {
      this.documentos = response;
      console.log('Response: ', this.documentos);
    });

  }


  visualizar(uuid: string) {
    console.log('Visualizar por UUID: ', uuid);

    this.selectedUUID = uuid;
    this.documentoService.visualizarFromUUID(uuid).subscribe(response => {
      this.pdfbase64 = response;
      this.showDialog = true;
      setTimeout(() => {
        this.pesquisarOcorrencias(this.filtro.texto);
      }, 1000);
    }, error => {

      console.log('ERRO 001: ', error);


      this.messageService.add({
        severity: 'error',
        summary: 'Erro',
        detail: error.error
      });
    })
  }


  excluir(uuid: string) {
    console.log('Excluir por UUID: ', uuid);
    this.documentoService.deleteFromUUID(uuid).subscribe(response => {
      this.pesquisar();
    });
  }

  onFileSelected(event: any) {

    console.log('Arquivo Selecionado...');

    const file: File = event.target.files[0];

    if (!FileValidator.isValidType(file)) {
      this.messageService.add({
        severity: 'error',
        summary: 'Erro',
        detail: `Tipo de arquivo não permitido. Tipo atual: ${file.type}`
      });

      event.target.value = '';
      return;
    }


    if (!FileValidator.isValidSize(file)) {
      this.messageService.add({
        severity: 'error',
        summary: 'Erro',
        detail: `Tamanho máximo permitido é 40MB. Arquivo atual: ${(file.size / 1024 / 1024).toFixed(2)}MB`
      });

      event.target.value = '';
      return;
    }

    console.log('ARQUIVO: ', file.type)

    if (file.type == 'video/mp4') {
      console.log('UPLOAD VIDEO: ', file.type)

      this.documentoService.uploadFile(file).subscribe({
        next: () => {
          this.messageService.add({severity: 'success', summary: 'Sucesso', detail: 'Upload realizado para o MinIO'});
        },
        error: err => {
          this.messageService.add({severity: 'error', summary: 'Erro', detail: err.error.value});
        }
      });

      return;
    }


    this.documentoService.uploadPdf(file).subscribe({
      next: (res) => {
        this.messageService.add({severity: 'success', summary: 'Sucesso', detail: 'Upload realizado com sucesso'});
        console.log('Arquivo salvo com UUID:', res);
      },
      error: (err) => {
        this.messageService.add({severity: 'error', summary: 'Erro', detail: err.error.value});
        console.log('Erro ao enviar arquivo', err);
      }
    });
  }

  pesquisarOcorrencias(termo: string): void {
    this.ngxExtendedPdfViewerService.find(termo, {
      highlightAll: true,
      findMultiple: true,
      wholeWords: true,
      useSecondaryFindcontroller: false
    });
  }

  antetiorOcorrencia(termo: string): void {
    this.ngxExtendedPdfViewerService.findPrevious();
  }

  proximaOcorrencia(termo: string): void {
    this.ngxExtendedPdfViewerService.findNext();
  }


  ArquivoAnterior(id: string): void {
    const index = this.documentos.findIndex(doc => doc.id === id);
    if (index > 0) {
      let uuid = this.documentos[index - 1].id;
      console.log('Documento anterior:', uuid);
      this.visualizar(uuid);
    } else {
      console.log('Não há documento anterior.');
    }
  }

  proximoArquivo(id: string): void {
    const index = this.documentos.findIndex(doc => doc.id === id);
    if (index !== -1 && index < this.documentos.length - 1) {
      let uuid = this.documentos[index + 1].id;
      console.log('Próximo documento:', uuid);
      this.visualizar(uuid);
    } else {
      console.log('Não há próximo documento.');
    }
  }

  downloadDocumento(uuid: string) {

    this.documentoService.getUrlPreAssinadaParaDownload(uuid).subscribe(url => {
      console.log('URL: ', url);
      window.open(url, '_blank');
    });

  }
}
