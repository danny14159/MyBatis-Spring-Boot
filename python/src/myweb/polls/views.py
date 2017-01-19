from django.shortcuts import render
import sys
import json
# Create your views here.
from django.http import HttpResponse
sys.path.append("..")
sys.path.append("..")
sys.path.append("..")
import db

def index(request):

    return HttpResponse(json.dumps(db.getData()))