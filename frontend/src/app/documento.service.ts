import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {catchError, from, map, Observable, switchMap, tap, throwError} from "rxjs";
import {Filtro} from "./filtro.model";

@Injectable({
  providedIn: 'root'
})
export class DocumentoService {


  baseUrl = "http://localhost:8080/documento";

  constructor(private httpClient: HttpClient) {
  }

  buscarPorConteudo(filtro: Filtro): Observable<any> {
    return this.httpClient.post<any>(`${this.baseUrl}/buscar-conteudo`, filtro);
  }

  visualizarFromUUID(uuid: string): Observable<string> {
    const url = `${this.baseUrl}/download/base64/${uuid}`;
    return this.httpClient.get(url, {responseType: 'text'});
  }


  uploadPdf(arquivo: File): Observable<string> {
    const formData = new FormData();
    formData.append('file', arquivo);
    return this.httpClient.post<string>(`${this.baseUrl}/upload`, formData);
  }

  deleteFromUUID(uuid: string): Observable<Object> {
    return this.httpClient.delete(`${this.baseUrl}/${uuid}`);
  }


  uploadFile(file: File): Observable<void> {
    return this.getPresignedUrl().pipe(
      tap(presignedUrl => {
        // console.log('URL Pré-Assinada:', presignedUrl);
      }),
      switchMap(presignedUrl => this.uploadToMinIO(presignedUrl, file)),
      catchError(err => {
        console.error('Erro no upload:', err);
        return throwError(() => new Error('Upload falhou'));
      })
    );
  }

  private getPresignedUrl(): Observable<string> {
    const url = `${this.baseUrl}/presigned-url`;
    return this.httpClient.get<{ value: string }>(url).pipe(map(reponse => reponse.value));
  }

  private uploadToMinIO(presignedUrl: string, file: File): Observable<any> {
    console.log('URL: ', presignedUrl);
    const headers = new HttpHeaders({
      'Content-Type': file.type
    });

    return from(
      fetch(presignedUrl, {
        method: 'PUT',
        body: file,
        headers: {
          'Content-Type': file.type
        }
      }).then(response => {
        if (!response.ok) {
          throw new Error('Falha no upload');
        }
      })
    );

    // return this.httpClient.put(presignedUrl, file, {headers, responseType: 'text'});
  }


}
