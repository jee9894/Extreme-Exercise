# Import firebase_admin, credential, database module
import firebase_admin
from firebase_admin import credentials, db

# DTW알고리즘, 이동평균을 이용하여 운동수행능력을 평가하기위한 모듈 추가 
from math import *
from matplotlib import pyplot as plt
#from scipy.spatial.distance import euclidean
#from fastdtw import fastdtw
import numpy as np
import pandas as pd
import sys
import time
import copy

###############################################################################################
###############################################################################################
###############################################################################################
# 해당 세트의 튀는 값 보정
def setCorr(data, threshold, tps):
    i = 0
    corr_time = 0
    m = 0
    
    while i < len(data) - 1:
        if (data[i] >= threshold and data[i+1] < threshold):
            j = i + 1
            
            while j < len(data) and data[j] < threshold:
                j += 1
                if(data[j-1] > m):
                    m = data[j-1]
                    
            corr_time = (j - i - 1) * 0.1
            
            if (corr_time < tps / 4):
                for k in range(i + 1, j):
                    data[k] = m
        i += 1
        m = 0
            
    return data
            

# 해당 세트의 데이터를 받아와 유효한 횟수 검증 및 총 피크시간 반환
def setCheck(data, threshold, tps):
    total_peak = 0
    peak_time = 0
    times = 0
    i = 0
    
    while i < len(data):
        if (data[i] >= threshold):
            j = i + 1
            while j < len(data) and data[j] >= threshold:
                j += 1
            peak_time = (j - i) * 0.1
            total_peak += peak_time
            if (peak_time >= tps):
                times += 1
            i = j
            
        else :
            i += 1

    return len(data)*0.1, times, total_peak

# 점수 메기기
def setScore(thres, nps, times, tpot):
    proper_thres = (1 / 784**2) * (thres**2)
    proper_times = times / nps
    proper_tpot = -(4 / 25) * tpot * (tpot - 5)
    proper_nps = (0.1 * nps) / (nps - times + 1)

    return proper_thres + proper_times + proper_tpot + proper_nps
    
# 데이터 matplotlib로 그리기
def plotData(data):
    time = [i*0.1 for i in range(0, len(data))]
    plt.plot(time, data)
    plt.xlabel('Time'); plt.ylabel('Value'); plt.title('Data')
    plt.tight_layout()
    plt.show()
    #plt.savefig('c://Users/jun/Desktop/' + temp + '.png', dpi=300)

# 파이어베이스 접속 
def firebaseAccess():
    # Fetch the service account key JSON file contents
    # 서비스 계정 키 JSON 파일 내용 가져 오기
    cred = credentials.Certificate('C:/Users/jun/Documents/ExtremeExercise_Python/ExtremeExerciseKey.json')

    # Initialize the app with a service account, granting admin privileges
    # 관리자 권한을 부여하고 서비스 계정으로 앱 초기화
    firebase_admin.initialize_app(cred, {
        'databaseURL': 'https://sexy-2019.firebaseio.com/'
    })
    
    # As an admin, the app has access to read and write all data, regradless of Security Rules
    # 관리자는 보안 규칙과 상관없이 앱에서 모든 데이터를 읽고 쓸 수 있음
    root = db.reference()
    
    # root밑의 user 로부터 사용자 ID리스트를 받아와서 비교
    user = root.child('user')
    
    print('Firebase Access Success')
    return user

###############################################################################################
###############################################################################################
###############################################################################################






user = firebaseAccess()

while True:
    
    # 데이터 베이스 내의 모든 user 리스트
    user_list = list(user.get().keys())
    print('유저 리스트 : ', user_list)

    # for문을 돌면서 무보정 데이터를 바꿈
    for users in user_list:
        if not ('kind' in user.child(users).get().keys()):
            print(users, '은(는) 운동 측정한 적이 없습니다.(1)',)
            continue
        
        # 한 유저가 가지고 있는 운동 종류 리스트
        kind = user.child(users).child('kind')
        kind_list = list(kind.get().keys())
        print(users,'은(는)', kind_list, '운동을 하였습니다.(2)')

        # kind 밑의 운동종류의 운동부위 리스트 
        for temp2 in kind_list:
            part = kind.child(temp2)
            if not (part.get()):
                print(users, '은(는)', temp2, '을(를)한 적이 없습니다.(3)')
                continue
            part_list = list(part.get().keys())
            print(users, '은(는)', temp2, '에서', part_list, '의 운동을 하였습니다.(4)')

            # 운동부위별 EX리스트
            for temp3 in part_list:
                exercise = part.child(temp3)
                if not (exercise.get()):
                    print(users, '은(는)', temp3, '을(를)한 적이 없습니다.(5)')
                    continue
                ex_list = list(exercise.get().keys())
                print(users, '은(는)', temp3, '에서', ex_list, '만큼 운동을 하였습니다.(6)')

                if ('sample' in ex_list):
                    ex_list.remove('sample')
                    ex_list.insert(0, 'sample')
                
                # 한 세트 내의 데이터 보정
                for temp4 in ex_list:
                    ex_data = exercise.child(temp4)
                    data_dict = ex_data.get()
                    
                    # 무보정 데이터
                    if (data_dict.get('correction') == 'no'):
                        if not ('data' in data_dict.keys()):
                            print(temp4, '세트에 대한 데이터가 없습니다.(7)')
                            continue

                        if not ('tpot' in data_dict.keys()):
                            print(temp4, '세트에 대한 topt가 없습니다.(8)')
                            continue

                        # 무보정 데이터 파이어 베이스에서 가져오기
                        value = data_dict.get('data').split(',')
                        value = [int(i) for i in value]

                        # tpot 값 가져오기
                        tpot = int(data_dict.get('tpot'))

                        # nps 값 가져오기
                        nps = int(data_dict.get('nps'))

                        # threshold 값 가져오기
                        if (temp4 == 'sample'):
                            threshold = max(value) * 0.8
                        else :
                            threshold = int(exercise.child('sample').get().get('threshold'))
                        
                        # 데이터 보정
                        # threshold : 데이터 유효한계치(근전도 데이터 최대치의 80%)
                        # tpot : 운동 1회당 걸리는 시간
                        value = setCorr(value, threshold, tpot)

                        # 데이터 이동평균
                        value_ma = pd.Series(value).rolling(window=5, min_periods=1, center=True).mean()
                        # 데이터 총 시간, 유효 횟수, 총 피크시간 체크
                        total, times, peak = setCheck(value_ma, threshold, tpot)
                        # 분석된 데이터를 통해 운동 수행에 대한 평가 점수 매기기
                        score = setScore(threshold, nps, times, tpot)

                        # 데이터를 파이어베이스에 넣을 수 있도록 만듦
                        value_ma = [int(i) for i in value_ma]
                        value_ma = [str(i) for i in value_ma]
                        value_ma = ','.join(value_ma)

                        # 파이어베이스에 업데이트
                        ex_data.update({'data':value_ma})
                        ex_data.update({'correction':'yes'})
                        ex_data.update({'total time':total})
                        ex_data.update({'valid times':times})
                        ex_data.update({'total peak':peak})
                        ex_data.update({'threshold':threshold})
                        ex_data.update({'score':float(format(score, '.3f'))})
                        
    for i in range(0, 5):
        print('time break', i + 1)
        time.sleep(1)
