import {Component, ViewEncapsulation} from '@angular/core';
import {Filtro} from "./filtro.model";
import {DocumentoService} from "./documento.service";
import {Documento} from "./documento.model";
import {NgxExtendedPdfViewerService} from "ngx-extended-pdf-viewer";

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
              private ngxExtendedPdfViewerService: NgxExtendedPdfViewerService) {
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
    this.documentoService.uploadPdf(file).subscribe({
      next: (res) => {
        console.log('Arquivo salvo com UUID:', res);
      },
      error: (err) => {
        console.error('Erro ao enviar arquivo', err);
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
}
